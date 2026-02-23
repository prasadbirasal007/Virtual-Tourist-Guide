document.addEventListener("DOMContentLoaded", function () {

    fetch("PlaceServlet?type=profile")
        .then(response => response.json())
        .then(data => {

            // If not logged in
            if (data.error) {
                window.location.href = "login.html";
                return;
            }

            const ratingsList = document.getElementById("ratingsList");
            const visitList = document.getElementById("visitList");
            const username = document.getElementById("username");
            const email = document.getElementById("email");

            // Clear old data
            ratingsList.innerHTML = "";
            visitList.innerHTML = "";

            // ======================
            // Show Ratings
            // ======================
            if (data.ratings.length === 0) {
                ratingsList.innerHTML = "<li>No ratings yet</li>";
            } else {
                data.ratings.forEach(item => {
                    ratingsList.innerHTML +=
                        `<li>${item.place} ⭐ ${item.rating}/5</li>`;
                });
            }

            // ======================
            // Show Visits
            // ======================
            if (data.visits.length === 0) {
                visitList.innerHTML = "<li>No visits yet</li>";
            } else {
                data.visits.forEach(place => {
                    visitList.innerHTML +=
                        `<li>${place}</li>`;
                });
            }

            // Optional display
            username.innerText = "Welcome Back!";
            email.innerText = "Your personal activity summary";

        })
        .catch(error => {
            console.error("Profile loading error:", error);
        });

});
