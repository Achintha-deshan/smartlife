document.addEventListener("DOMContentLoaded", function () {
    const BASE_URL = "http://localhost:8080";
    const token = localStorage.getItem('token');
    const userRole = localStorage.getItem('role');

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    if (userRole === 'MEMBER') {

        $(".admin-only").hide();

        const expenseSection = document.getElementById('daily-expense-section');
        if(expenseSection) expenseSection.style.display = 'block';

        $(".nav-item-reminders").show();
    }

    const currentMonth = new Date().toISOString().slice(0, 7);

    loadDailyLimit(BASE_URL, token, currentMonth);

    if (userRole === 'ADMIN') {
        loadSavingsBalance(BASE_URL, token);
    }
});


function loadDailyLimit(baseUrl, token, month) {
    fetch(`${baseUrl}/api/v1/finance/daily-limit/${month}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Daily limit fetch failed');
            return response.json();
        })
        .then(res => {
            const element = document.getElementById("daily-limit-text");
            if (element && res.data !== undefined) {
                element.innerText = `Rs. ${parseFloat(res.data).toLocaleString()}`;
            }
        })
        .catch(error => console.error("Error:", error));
}

function loadSavingsBalance(baseUrl, token) {
    fetch(`${baseUrl}/api/v1/savings/balance`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Savings fetch failed');
            return response.json();
        })
        .then(data => {
            const element = document.getElementById("total-savings-text");
            if (element) {
                element.innerText = `Rs. ${data.totalBalance.toLocaleString()}`;
            }
        })
        .catch(error => console.error("Error:", error));
}