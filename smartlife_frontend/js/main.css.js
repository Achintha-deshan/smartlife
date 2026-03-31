document.addEventListener("DOMContentLoaded", function() {
    fetch('sidebar.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('sidebar-container').innerHTML = data;

            const currentPath = window.location.pathname.split("/").pop();
            const links = document.querySelectorAll('.nav-links li a');

            links.forEach(link => {
                if (link.getAttribute('href') === currentPath) {
                    link.parentElement.classList.add('active');
                } else {
                    link.parentElement.classList.remove('active');
                }
            });
        })
        .catch(err => console.error("not lord side bar:", err));
});