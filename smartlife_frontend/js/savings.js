const SAVINGS_API = "http://localhost:8080/api/v1/savings";

$(document).ready(function () {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "signinup.html";
        return;
    }

    $.ajaxSetup({
        headers: { 'Authorization': 'Bearer ' + token }
    });

    loadSavingsDashboard();

    $('#withdrawForm').on('submit', function (e) {
        e.preventDefault();
        const amount = $('#txtWithdrawAmount').val();

        if (!amount || amount <= 0) {
            alert("Please enter a valid withdrawal amount.");
            return;
        }

        if (confirm(`Are you sure you want to withdraw Rs. ${parseFloat(amount).toLocaleString()} from your total savings?`)) {
            processWithdrawal(amount);
        }
    });
});

function loadSavingsDashboard() {
    $.ajax({
        url: `${SAVINGS_API}/balance`,
        type: "GET",
        success: function (res) {
            const totalBalance = (res && res.totalBalance !== undefined) ? res.totalBalance : 0;

            $('#lblTotalSavings').text("Rs. " + totalBalance.toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }));

            const lastUpdateText = res.lastUpdated ? new Date(res.lastUpdated).toLocaleString() : "No recent activity";
            $('#lblLastUpdated').text("Last Transaction: " + lastUpdateText);
        },
        error: function(xhr) {
            console.error("Balance Load Error:", xhr.responseText);
            $('#lblTotalSavings').text("Rs. 0.00");
        }
    });

    $.ajax({
        url: `${SAVINGS_API}/history`,
        type: "GET",
        success: function (res) {
            updateHistoryTable(res);
        },
        error: function(xhr) {
            console.error("History Load Error:", xhr.responseText);
        }
    });
}

function processWithdrawal(amount) {
    $.ajax({
        url: `${SAVINGS_API}/withdraw?amount=${amount}`,
        type: "POST",
        success: function (res) {
            alert("Withdrawal successful! Your savings have been updated.");
            $('#txtWithdrawAmount').val('');
            loadSavingsDashboard();
        },
        error: function (xhr) {
            alert("Transaction Failed: " + xhr.responseText);
        }
    });
}

function updateHistoryTable(transactions) {
    const tableBody = $("#savingsHistoryTable");
    tableBody.empty();

    if (!transactions || transactions.length === 0) {
        tableBody.append('<tr><td colspan="4" style="text-align:center; color:#999;">No transaction records found.</td></tr>');
        return;
    }

    transactions.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

    transactions.forEach(item => {
        let color = "#1cc88a";
        let prefix = "+";

        if (item.transactionType === "WITHDRAWAL") {
            color = "#e74a3b";
            prefix = "-";
        }

        const date = new Date(item.dateTime).toLocaleDateString();
        const time = new Date(item.dateTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

        const row = `
            <tr>
                <td>${date} <br><small style="color:#999">${time}</small></td>
                <td><span class="badge" style="background:${color}15; color:${color}; border: 1px solid ${color}30; padding: 4px 8px; border-radius: 4px; font-size: 11px;">
                    ${item.transactionType.replace('_', ' ')}
                </span></td>
                <td>${item.referenceMonth || 'N/A'}</td>
                <td style="text-align: right; font-weight: bold; color: ${color}">
                    ${prefix} ${item.amount.toLocaleString(undefined, {minimumFractionDigits: 2})}
                </td>
            </tr>`;
        tableBody.append(row);
    });
}