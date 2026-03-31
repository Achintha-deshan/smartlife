$(function() {
    $("#sidebar-placeholder").load("sidebar.html", function(response, status, xhr) {
        if (status == "error") return;

        let role = localStorage.getItem("role");
        console.log("Current Logged Role:", role);

        if (role === "MEMBER") {

            $(".nav-links li").hide();


            $(".nav-links li").each(function() {
                let linkText = $(this).text().toLowerCase();

                if (linkText.includes("daily expenses") || linkText.includes("smart reminders")) {
                    $(this).show();
                }
            });

            $(".admin-only").remove();

            $(".u-info small").text("Family Member");

        } else if (role === "ADMIN") {
            $(".nav-links li").show();
            $(".u-info small").text("Administrator");
        }

        let current = window.location.pathname.split("/").pop();
        $('.nav-links a').each(function() {
            if ($(this).attr('href').includes(current)) {
                $(this).closest('li').addClass('active');
            }
        });

        $("#mobile-close, .close-sidebar-btn").on("click", function() {
            $(".sidebar").removeClass("active");
        });
    });

    $(document).on("click", "#global-menu-trigger", function() {
        $(".sidebar").addClass("active");
    });

    $(document).on("click", function(e) {
        if ($(".sidebar").hasClass("active")) {
            if (!$(e.target).closest('.sidebar').length && !$(e.target).closest('#global-menu-trigger').length) {
                $(".sidebar").removeClass("active");
            }
        }
    });
});