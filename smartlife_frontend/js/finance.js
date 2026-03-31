const BASE_URL = "http://localhost:8080/api/v1/finance";

$(document).ready(function () {
    const token = localStorage.getItem("token");
    const currentAdminId = localStorage.getItem("userId");

    if (!token || !currentAdminId) {
        window.location.href = "../pages/signinup.html";
        return;
    }

    $.ajaxSetup({
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    });

    const currentMonth = new Date().toISOString().slice(0, 7);
    $('#salaryMonth').val(currentMonth);

    loadBudgetData(currentMonth);
    loadExpenses();

    $('#salaryMonth').on('change', function () {
        loadBudgetData($(this).val());
    });

    $('#salaryForm').on('submit', function (e) {
        e.preventDefault();

        const budgetData = {
            budgetMonth: $('#salaryMonth').val(),
            estimatedSalary: parseFloat($('#salaryAmount').val()) || 0,
            monthlySavingsGoal: parseFloat($('#savingsGoal').val()) || 0,
            isFinalized: $('#isFinalized').is(':checked'),
            isRecurring: $('#isRecurring').is(':checked'),
            adminId: currentAdminId
        };

        $.ajax({
            url: BASE_URL + "/budget",
            type: "POST",
            data: JSON.stringify(budgetData),
            success: function (res) {
                alert("Budget Updated Successfully!");
                updateDailyLimit(budgetData.budgetMonth);
            },
            error: function (xhr) {
                handleValidationErrors(xhr);
            }
        });
    });

    $('#loanForm').on('submit', function (e) {
        e.preventDefault();

        const loanData = {
            expenseTitle: $('#loanTitle').val(),
            monthlyAmount: parseFloat($('#loanAmount').val()),
            expiryDate: $('#loanEndDate').val(),
            adminId: currentAdminId
        };

        $.ajax({
            url: BASE_URL + "/expense",
            type: "POST",
            data: JSON.stringify(loanData),
            success: function () {
                alert("Fixed Expense Added!");
                $('#loanForm')[0].reset();
                loadExpenses();
                updateDailyLimit($('#salaryMonth').val());
            },
            error: function (xhr) {
                handleValidationErrors(xhr);
            }
        });
    });

    function loadBudgetData(month) {
        $.ajax({
            url: BASE_URL + "/budget/" + month,
            type: "GET",
            success: function (res) {
                if (res && res.data) {
                    $('#salaryAmount').val(res.data.estimatedSalary);
                    $('#savingsGoal').val(res.data.monthlySavingsGoal);
                    $('#isFinalized').prop('checked', res.data.isFinalized);
                    $('#isRecurring').prop('checked', res.data.isRecurring);
                    updateDailyLimit(month);
                } else {
                    resetBudgetFields();
                }
            },
            error: function () {
                resetBudgetFields();
            }
        });
    }

    function loadExpenses() {
        $.ajax({
            url: BASE_URL + "/expenses",
            type: "GET",
            success: function (res) {
                let tableBody = $("#loanTableBody");
                tableBody.empty();

                if (res.data && res.data.length > 0) {
                    $.each(res.data, function (i, item) {
                        let row = "<tr>" +
                            "<td>" + item.expenseTitle + "</td>" +
                            "<td>" + item.monthlyAmount + "</td>" +
                            "<td>" + (item.expiryDate || "N/A") + "</td>" +
                            "<td>Active</td>" +
                            "<td style='text-align: center;'><button class='btn-delete' onclick='deleteExpense(" + item.expenseId + ")'><i class='fas fa-trash'></i></button></td>" +
                            "</tr>";
                        tableBody.append(row);
                    });
                }
            }
        });
    }

    function updateDailyLimit(month) {
        $.ajax({
            url: BASE_URL + "/daily-limit/" + month,
            type: "GET",
            success: function (res) {
                let limitValue = (res && res.data !== undefined) ? res.data : res;
                let numericLimit = parseFloat(limitValue) || 0;

                $('#dailyLimitDisplay').text("Rs. " + numericLimit.toFixed(2));
                $('#lblDailyLimit').text(numericLimit.toFixed(2));
            },
            error: function () {
                $('#dailyLimitDisplay').text("Rs. 0.00");
                $('#lblDailyLimit').text("0.00");
            }
        });
    }

    window.deleteExpense = function (id) {
        if (confirm("Are you sure you want to delete this expense?")) {
            $.ajax({
                url: BASE_URL + "/expense/" + id,
                type: "DELETE",
                success: function () {
                    loadExpenses();
                    updateDailyLimit($('#salaryMonth').val());
                }
            });
        }
    };

    function handleValidationErrors(xhr) {
        const response = xhr.responseJSON;
        if (xhr.status === 400 && response.data) {
            let errorMsg = "";
            $.each(response.data, function (field, msg) {
                errorMsg += field + ": " + msg + "\n";
            });
            alert("Validation Failed:\n" + errorMsg);
        } else {
            alert("Error: " + (response ? response.message : "Request failed"));
        }
    }

    function resetBudgetFields() {
        $('#salaryAmount').val('');
        $('#savingsGoal').val('');
        $('#isFinalized').prop('checked', false);
        $('#dailyLimitDisplay').text("Rs. 0.00");
        $('#lblDailyLimit').text("0.00");
    }
});