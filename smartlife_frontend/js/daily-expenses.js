const EXPENSE_API_URL = "http://localhost:8080/api/v1/expenses";
const BUDGET_API_URL = "http://localhost:8080/api/v1/finance";

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

    loadDailyLimit(currentMonth);
    loadMonthExpenses();

    $('#dailyExpenseForm').on('submit', function (e) {
        e.preventDefault();

        const expenseData = {
            description: $('#expenseDesc').val(),
            amount: parseFloat($('#expenseAmount').val()) || 0,
            category: $('#expenseCategory').val(),
            date: $('#expenseDate').val() || new Date().toISOString().split('T')[0],
            adminId: currentAdminId
        };

        $.ajax({
            url: EXPENSE_API_URL + "/save",
            type: "POST",
            data: JSON.stringify(expenseData),
            success: function () {
                alert("Expense Saved Successfully!");
                $('#dailyExpenseForm')[0].reset();
                loadMonthExpenses();
                loadDailyLimit(currentMonth);
            },
            error: function (xhr) {
                handleValidationErrors(xhr);
            }
        });
    });

    $("#tableSearch").on("keyup", function() {
        let value = $(this).val().toLowerCase();
        $("#dailyExpenseTableBody tr").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});

function loadDailyLimit(month) {
    $.ajax({
        url: BUDGET_API_URL + "/daily-limit/" + month,
        type: "GET",
        success: function (res) {
            let limit = res.data !== undefined ? res.data : res;
            $('#lblDailyLimit').text(limit.toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }));
        }
    });
}

function loadMonthExpenses() {
    $.ajax({
        url: EXPENSE_API_URL + "/current-month",
        type: "GET",
        success: function (res) {
            refreshTable(res.data || res);
        }
    });
}

function refreshTable(expenses) {
    const tableBody = $("#dailyExpenseTableBody");
    tableBody.empty();

    if (!expenses || expenses.length === 0) {
        tableBody.append('<tr><td colspan="5" style="text-align:center;">No records found.</td></tr>');
        return;
    }

    $.each(expenses, function (i, exp) {
        let recorder = exp.recordedBy ? exp.recordedBy.nickname : "Admin";

        let row = "<tr>" +
            "<td>" + exp.date + "</td>" +
            "<td>" + exp.description + "<br><small>" + recorder + "</small></td>" +
            "<td>" + exp.category + "</td>" +
            "<td>" + exp.amount.toLocaleString() + "</td>" +
            "<td>" +
            "<button onclick='deleteExpense(" + exp.id + ")'>Delete</button>" +
            "</td>" +
            "</tr>";

        tableBody.append(row);
    });
}

window.deleteExpense = function (id) {
    if (confirm("Are you sure you want to delete this expense?")) {
        $.ajax({
            url: EXPENSE_API_URL + "/delete/" + id,
            type: "DELETE",
            success: function (res) {
                alert("Deleted!");
                const currentMonth = new Date().toISOString().slice(0, 7);
                loadMonthExpenses();
                loadDailyLimit(currentMonth);
            },
            error: function (xhr) {
                console.error(xhr);
                alert("Could not delete. Check backend logs.");
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