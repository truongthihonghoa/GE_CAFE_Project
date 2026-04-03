document.addEventListener("DOMContentLoaded", function () {
    const mockScheduleData = {
        "2026-03-16": {
            morning: { id: "LLV001", status: "Chưa gửi", created: "15/03/2026", employees: 3, details: [{ id: "NV001", name: "Nguyễn Văn An", role: "Pha chế", status: "Chưa gửi" }] },
            afternoon: { id: "LLV002", status: "Chưa gửi", created: "15/03/2026", employees: 3, details: [{ id: "NV002", name: "Trần Thị Bích", role: "Phục vụ", status: "Chưa gửi" }] },
            evening: { id: "LLV003", status: "Chưa gửi", created: "15/03/2026", employees: 0, details: [] },
        },
        "2026-03-17": {
            morning: { id: "LLV004", status: "Chưa gửi", created: "16/03/2026", employees: 6, details: [{ id: "NV003", name: "Lê Minh Cường", role: "Thu ngân", status: "Chưa gửi" }] },
            afternoon: { id: "LLV005", status: "Chưa gửi", created: "16/03/2026", employees: 6, details: [{ id: "NV004", name: "Phạm Thị Dung", role: "Phục vụ", status: "Chưa gửi" }] },
            evening: { id: "LLV006", status: "Chưa gửi", created: "16/03/2026", employees: 0, details: [] },
        },
        "2026-03-18": {
            morning: { id: "LLV007", status: "Đã gửi", created: "17/03/2026", employees: 8, details: [{ id: "NV005", name: "Hoàng Văn Em", role: "Giữ xe", status: "Đã gửi" }] },
            afternoon: { id: "LLV008", status: "Đã gửi", created: "17/03/2026", employees: 8, details: [{ id: "NV006", name: "Đỗ Thu Hà", role: "Phục vụ", status: "Đã gửi" }] },
            evening: { id: "LLV009", status: "Chưa gửi", created: "17/03/2026", employees: 3, details: [{ id: "NV007", name: "Ngô Thanh Long", role: "Pha chế", status: "Chưa gửi" }] },
        },
    };

    const shifts = [
        { key: "morning", label: "Ca Sáng", time: "06:00 - 12:00", defaultText: "Ca Sáng", className: "is-morning" },
        { key: "afternoon", label: "Ca Chiều", time: "12:00 - 17:00", defaultText: "Ca Chiều", className: "is-afternoon" },
        { key: "evening", label: "Ca Tối", time: "17:00 - 22:00", defaultText: "Ca Tối", className: "is-evening" },
    ];

    const weekdays = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"];
    const weekdayLabels = ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"];

    const scheduleBoard = document.getElementById("schedule-board");
    const miniCalendar = document.getElementById("mini-calendar");
    const calendarTitle = document.getElementById("calendar-title");
    const currentWeekLabel = document.getElementById("current-week-label");
    const prevWeekBtn = document.getElementById("prev-week-btn");
    const nextWeekBtn = document.getElementById("next-week-btn");
    const modal = document.getElementById("shift-detail-modal");
    const modalCloseBtn = document.getElementById("modal-close-btn");
    const modalConfirmBtn = document.getElementById("modal-confirm-btn");
    const modalSendBtn = document.getElementById("modal-send-notification-btn");
    const selectAllEmployees = document.getElementById("select-all-employees");

    let currentWeekStart = new Date("2026-03-16T00:00:00");
    let selectedDate = new Date("2026-03-16T00:00:00");

    function addDays(date, days) {
        const next = new Date(date);
        next.setDate(next.getDate() + days);
        return next;
    }

    function formatKey(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
    }

    function startOfWeek(date) {
        const start = new Date(date);
        const day = start.getDay();
        const offset = day === 0 ? -6 : 1 - day;
        start.setDate(start.getDate() + offset);
        start.setHours(0, 0, 0, 0);
        return start;
    }

    function formatShort(date) {
        return `${date.getDate()}/${date.getMonth() + 1}`;
    }

    function formatDateVN(date) {
        return `${String(date.getDate()).padStart(2, "0")}/${String(date.getMonth() + 1).padStart(2, "0")}/${date.getFullYear()}`;
    }

    function monthTitle(date) {
        return `Tháng ${date.getMonth() + 1} ${date.getFullYear()}`;
    }

    function monthTitleWithComma(date) {
        return `Tháng ${date.getMonth() + 1} ${date.getFullYear()}`;
    }

    function getWeekDays(startDate) {
        return Array.from({ length: 7 }, (_, index) => addDays(startDate, index));
    }

    function renderBoard() {
        const days = getWeekDays(currentWeekStart);
        const selectedDateKey = formatKey(selectedDate);
        scheduleBoard.innerHTML = "";

        const corner = document.createElement("div");
        corner.className = "board-corner";
        scheduleBoard.appendChild(corner);

        days.forEach((day, index) => {
            const header = document.createElement("div");
            const dateKey = formatKey(day);
            header.className = `board-day${dateKey === selectedDateKey ? " is-highlight" : ""}`;
            header.innerHTML = `${weekdayLabels[index]}<span class="date">${formatShort(day)}</span>`;
            scheduleBoard.appendChild(header);
        });

        shifts.forEach((shift) => {
            const timeLabel = document.createElement("div");
            timeLabel.className = "board-time";
            timeLabel.textContent = shift.label;
            scheduleBoard.appendChild(timeLabel);

            days.forEach((day) => {
                const dateKey = formatKey(day);
                const shiftData = mockScheduleData[dateKey]?.[shift.key];
                const button = document.createElement("button");
                button.type = "button";
                button.className = `shift-pill ${shift.className}${shiftData ? "" : " is-empty"}`;
                button.dataset.date = dateKey;
                button.dataset.shift = shift.key;
                button.textContent = shiftData && shiftData.employees > 0 ? `${shiftData.employees} người đăng ký` : shift.defaultText;

                if (shiftData) {
                    button.addEventListener("click", () => openModal(dateKey, shift.key));
                }

                scheduleBoard.appendChild(button);
            });
        });

        currentWeekLabel.textContent = `${formatShort(days[0])} - ${formatShort(days[6])}`;
        calendarTitle.textContent = monthTitleWithComma(days[0]);
    }

    function renderMiniCalendar() {
        const baseDate = currentWeekStart;
        const selectedDateKey = formatKey(selectedDate);
        const year = baseDate.getFullYear();
        const month = baseDate.getMonth();
        const firstDay = new Date(year, month, 1);
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const startOffset = (firstDay.getDay() + 6) % 7;

        miniCalendar.innerHTML = "";
        weekdays.forEach((label) => {
            const weekday = document.createElement("div");
            weekday.className = "mini-calendar-weekday";
            weekday.textContent = label;
            miniCalendar.appendChild(weekday);
        });

        for (let i = 0; i < startOffset; i += 1) {
            const empty = document.createElement("div");
            miniCalendar.appendChild(empty);
        }

        for (let day = 1; day <= daysInMonth; day += 1) {
            const date = new Date(year, month, day);
            const dateKey = formatKey(date);
            const cell = document.createElement("div");
            cell.className = `mini-calendar-day${dateKey === selectedDateKey ? " is-selected" : ""}`;
            cell.textContent = day;
            miniCalendar.appendChild(cell);
        }

        calendarTitle.textContent = monthTitle(baseDate);
    }

    function openModal(dateKey, shiftKey) {
        const shiftData = mockScheduleData[dateKey]?.[shiftKey];
        if (!shiftData) return;

        const shiftInfo = shifts.find((item) => item.key === shiftKey);
        document.getElementById("modal-schedule-id").textContent = shiftData.id || "N/A";
        document.getElementById("modal-date").textContent = formatDateVN(new Date(`${dateKey}T00:00:00`));
        document.getElementById("modal-shift-time").textContent = `${shiftInfo.defaultText} (${shiftInfo.time})`;
        document.getElementById("modal-status").textContent = shiftData.status || "Chưa có";
        document.getElementById("modal-created-date").textContent = shiftData.created || "N/A";

        const employeeListBody = document.getElementById("modal-employee-list");
        employeeListBody.innerHTML = "";

        if (shiftData.details.length) {
            shiftData.details.forEach((employee) => {
                const isSent = employee.status === "Đã gửi";
                const row = document.createElement("tr");
                row.className = isSent ? "is-sent" : "";
                row.dataset.employeeId = employee.id;
                row.innerHTML = `
                    <td><input type="checkbox" ${isSent ? "disabled" : ""}></td>
                    <td>${employee.id}</td>
                    <td>${employee.name}</td>
                    <td>${employee.role}</td>
                    <td class="status-cell">${employee.status}</td>
                `;
                employeeListBody.appendChild(row);
            });
        } else {
            employeeListBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Chưa có nhân viên nào trong ca này.</td></tr>';
        }

        modalSendBtn.dataset.date = dateKey;
        modalSendBtn.dataset.shift = shiftKey;
        selectAllEmployees.checked = false;
        modal.classList.add("show");
    }

    function closeModal() {
        modal.classList.remove("show");
    }

    function handleSendNotification() {
        const checkedBoxes = modal.querySelectorAll("tbody input[type='checkbox']:checked");
        if (!checkedBoxes.length) {
            alert("Vui lòng chọn ít nhất một nhân viên để gửi thông báo.");
            return;
        }
        alert("Đã gửi thông báo thành công!");
        closeModal();
    }

    modalCloseBtn.addEventListener("click", closeModal);
    modalConfirmBtn.addEventListener("click", closeModal);
    modalSendBtn.addEventListener("click", handleSendNotification);

    modal.addEventListener("click", function (event) {
        if (event.target === modal) {
            closeModal();
        }
    });

    selectAllEmployees.addEventListener("change", function (event) {
        const checkboxes = modal.querySelectorAll("tbody input[type='checkbox']:not(:disabled)");
        checkboxes.forEach((checkbox) => {
            checkbox.checked = event.target.checked;
        });
    });

    prevWeekBtn.addEventListener("click", function () {
        currentWeekStart = addDays(currentWeekStart, -7);
        selectedDate = addDays(selectedDate, -7);
        renderBoard();
        renderMiniCalendar();
    });

    nextWeekBtn.addEventListener("click", function () {
        currentWeekStart = addDays(currentWeekStart, 7);
        selectedDate = addDays(selectedDate, 7);
        renderBoard();
        renderMiniCalendar();
    });

    currentWeekStart = startOfWeek(selectedDate);
    renderBoard();
    renderMiniCalendar();
});
