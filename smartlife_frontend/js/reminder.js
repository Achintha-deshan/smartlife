const BASE_URL = "http://localhost:8080/api/v1/reminders";
let playingAlerts = new Set();

document.addEventListener("DOMContentLoaded", () => {
    loadReminders();
    setInterval(checkAndPlayAlerts, 5000);
});

function saveNewReminder() {
    const title = document.getElementById('rem-title').value;
    const dateTime = document.getElementById('rem-datetime').value;
    const token = localStorage.getItem('token');

    if (!title || !dateTime) {
        alert("Please fill all fields!");
        return;
    }

    const payload = {
        title: title,
        dateTime: dateTime
    };

    fetch(`${BASE_URL}/add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (res.ok) {
                alert("Reminder set successfully! 🚀");
                document.getElementById('rem-title').value = "";
                document.getElementById('rem-datetime').value = "";
                loadReminders();
            } else {
                alert("Failed to save reminder.");
            }
        })
        .catch(err => console.error("Error:", err));
}

function loadReminders() {
    const token = localStorage.getItem('token');
    const listContainer = document.getElementById('reminder-list');

    fetch(`${BASE_URL}/my-pending`, {
        headers: { 'Authorization': `Bearer ${token}` }
    })
        .then(res => res.json())
        .then(data => {
            listContainer.innerHTML = "";
            if (data.length === 0) {
                listContainer.innerHTML = '<p class="empty-msg">No upcoming alerts</p>';
                return;
            }

            data.forEach(rem => {
                const isBirthday = rem.title.toLowerCase().includes("birthday");
                const dateStr = new Date(rem.reminderDateTime).toLocaleString();

                const itemHtml = `
                <div class="rem-item ${isBirthday ? 'bday-theme' : ''}">
                    <div class="rem-meta">
                        <strong>${rem.title}</strong>
                        <span><i class="far fa-clock"></i> ${dateStr}</span>
                    </div>
                    <button class="delete-btn" onclick="markAsDone(${rem.reminderId})">
                        <i class="fas fa-check"></i>
                    </button>
                </div>
            `;
                listContainer.innerHTML += itemHtml;
            });
        })
        .catch(err => console.error("Error loading list:", err));
}

function checkAndPlayAlerts() {
    const token = localStorage.getItem('token');
    const now = new Date();

    fetch(`${BASE_URL}/my-pending`, {
        headers: { 'Authorization': `Bearer ${token}` }
    })
        .then(res => res.json())
        .then(data => {
            data.forEach(rem => {
                const remTime = new Date(rem.reminderDateTime);
                if (now >= remTime && !playingAlerts.has(rem.reminderId)) {
                    triggerAlert(rem);
                }
            });
        })
        .catch(err => console.error("Polling error:", err));
}

function triggerAlert(reminder) {
    playingAlerts.add(reminder.reminderId);

    const isBirthday = reminder.title.toLowerCase().includes("birthday");
    const audio = document.getElementById(isBirthday ? 'birthday-audio' : 'normal-audio');

    audio.currentTime = 0;
    audio.loop = true;

    audio.play().then(() => {
        setTimeout(() => {
            if (confirm(`⏰ REMINDER: ${reminder.title}\n\nClick OK to stop the music and dismiss.`)) {
                audio.pause();
                audio.currentTime = 0;
                markAsDone(reminder.reminderId);
            }
        }, 300);
    }).catch(err => {
        console.warn("Autoplay blocked. Waiting for click.");
        if (confirm(`⏰ REMINDER: ${reminder.title}`)) {
            markAsDone(reminder.reminderId);
        }
    });
}

function markAsDone(id) {
    const token = localStorage.getItem('token');

    fetch(`${BASE_URL}/process/${id}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(res => {
            if (res.ok) {
                playingAlerts.delete(id);
                loadReminders();
            }
        })
        .catch(err => {
            console.error("Failed to mark as done:", err);
            playingAlerts.delete(id);
        });
}