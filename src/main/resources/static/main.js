document.querySelectorAll(".drop-zone__input").forEach((inputElement) => {
    const dropZoneElement = inputElement.closest(".drop-zone");

    dropZoneElement.addEventListener("click", (e) => {
        console.log("Drop zone clicked!");
        inputElement.click();
    });

    inputElement.addEventListener("change", (e) => {
        console.log("File selected via input!");
        if (inputElement.files.length) {
            const file = inputElement.files[0];
            console.log("Selected file:", file.name, file.size, file.type);
            updateThumbnail(dropZoneElement, file);
            uploadFile(file);
        }
    });

    dropZoneElement.addEventListener("dragover", (e) => {
        e.preventDefault();
        console.log("File dragged over drop zone!");
        dropZoneElement.classList.add("drop-zone--over");
    });

    ["dragleave", "dragend"].forEach((type) => {
        dropZoneElement.addEventListener(type, (e) => {
            console.log(`Drag event: ${type}`);
            dropZoneElement.classList.remove("drop-zone--over");
        });
    });

    dropZoneElement.addEventListener("drop", (e) => {
        e.preventDefault();
        console.log("File dropped!");

        if (e.dataTransfer.files.length) {
            const file = e.dataTransfer.files[0];
            console.log("Dropped file:", file.name, file.size, file.type);
            inputElement.files = e.dataTransfer.files;
            updateThumbnail(dropZoneElement, file);
            uploadFile(file); // Добавлен вызов функции загрузки
        }

        dropZoneElement.classList.remove("drop-zone--over");
    });
});

function updateThumbnail(dropZoneElement, file) {
    let thumbnailElement = dropZoneElement.querySelector(".drop-zone__thumb");


    if (dropZoneElement.querySelector(".drop-zone__prompt")) {
        dropZoneElement.querySelector(".drop-zone__prompt").remove();
    }

    if (!thumbnailElement) {
        thumbnailElement = document.createElement("div");
        thumbnailElement.classList.add("drop-zone__thumb");
        dropZoneElement.appendChild(thumbnailElement);
    }

    thumbnailElement.dataset.label = file.name;

    if (file.type.startsWith("image/")) {
        const reader = new FileReader();

        reader.readAsDataURL(file);
        reader.onload = () => {
            thumbnailElement.style.backgroundImage = `url('${reader.result}')`;
        };
    } else {
        thumbnailElement.style.backgroundImage = null;
    }
}

function uploadFile(file) {
    console.log("Starting file upload...");

    const formData = new FormData();
    formData.append("file", file);

    fetch("/upload", {
        method: "POST",
        body: formData,
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then((data) => {
            console.log("File uploaded successfully:", data);
            alert("File uploaded successfully!");
        })
        .catch((error) => {
            console.error("Error uploading file:", error);
            alert("Error uploading file.");
        });
}

    function openRenameModal(button) {
    const path = button.getAttribute('data-path');
    const modal = document.getElementById('renameModal');
    const form = modal.querySelector('form');
    form.querySelector('input[name="path"]').value = path;
    modal.style.display = 'block';
}

    function openMoveModal(button) {
    const path = button.getAttribute('data-path');
    const modal = document.getElementById('moveModal');
    const form = modal.querySelector('form');
    form.querySelector('input[name="path"]').value = path;
    modal.style.display = 'block';
}

    function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function openCreateFolderModal() {
    document.getElementById('createFolderModal').style.display = 'block';
}
document.querySelectorAll('[href="#renameModal"]').forEach(button => {
    button.addEventListener('click', () => {
        document.getElementById('renameModal').style.display = 'block';
    });
});

document.querySelectorAll('[href="#moveModal"]').forEach(button => {
    button.addEventListener('click', () => {
        document.getElementById('moveModal').style.display = 'block';
    });
});

