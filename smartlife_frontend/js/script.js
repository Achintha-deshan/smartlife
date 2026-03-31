const API_BASE_URL = "http://localhost:8080/api/v1/auth";

$(document).ready(function () {
    $('input').val('');

    const savedTheme = localStorage.getItem('selectedTheme');
    if (savedTheme === 'dark') {
        $('html').attr('data-theme', 'dark');
        $('#themeToggleBtn i').removeClass('fa-moon').addClass('fa-sun');
    }

    $('#loginForm').on('submit', function (e) {
        e.preventDefault();

        const loginData = {
            email: $('#loginEmail').val(),
            password: $('#loginPass').val()
        };

        $.ajax({
            url: `${API_BASE_URL}/signin`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(loginData),
            success: function (result) {
                const token = result.data.access_token;
                const role = result.data.role;
                const userId = result.data.userId;
                const adminId = result.data.adminId;

                if (!token) {
                    alert("Token not found!");
                    return;
                }

                localStorage.setItem('token', token);
                localStorage.setItem('role', role);
                localStorage.setItem('userId', userId);
                localStorage.setItem('adminId', role === 'ADMIN' ? userId : adminId);

                alert("Login Successful!");
                window.location.href = role === 'MEMBER'
                    ? "../pages/member-dashbord.html"
                    : "../pages/dashbord.html";
            },
            error: function (xhr) {
                alert("Login Failed: " + (xhr.responseJSON ? xhr.responseJSON.message : "Invalid Credentials"));
            }
        });
    });

    $('#signupForm').on('submit', function (e) {
        e.preventDefault();

        $.ajax({
            url: `${API_BASE_URL}/signup`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                fullName: $('#signupName').val(),
                email: $('#signupEmail').val(),
                password: $('#signupPass').val(),
                role: "ADMIN"
            }),
            success: function () {
                alert("Registration Successful!");
                switchToLogin();
            },
            error: function (xhr) {
                alert("Registration Failed: " + (xhr.responseJSON ? xhr.responseJSON.message : "Error"));
            }
        });
    });
});

function switchToRegister() { $('input').val(''); $('#mainContainer').addClass('register-active'); }
function switchToLogin() { $('input').val(''); $('#mainContainer').removeClass('register-active'); }

function togglePasswordVisibility(inputId, iconElement) {
    const input = $(`#${inputId}`);
    const icon = $(iconElement);
    if (input.attr('type') === 'password') {
        input.attr('type', 'text');
        icon.removeClass('fa-eye').addClass('fa-eye-slash');
    } else {
        input.attr('type', 'password');
        icon.removeClass('fa-eye-slash').addClass('fa-eye');
    }
}

function toggleTheme() {
    const html = $('html');
    const icon = $('#themeToggleBtn i');
    if (html.attr('data-theme')) {
        html.removeAttr('data-theme');
        localStorage.setItem('selectedTheme', 'light');
        icon.removeClass('fa-sun').addClass('fa-moon');
    } else {
        html.attr('data-theme', 'dark');
        localStorage.setItem('selectedTheme', 'dark');
        icon.removeClass('fa-moon').addClass('fa-sun');
    }
}