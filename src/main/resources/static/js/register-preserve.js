document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('.register-form');
    if (!form) return;

    const storageKey = 'registerFormData';
    const fieldNames = [
        'fullName',
        'email',
        'password',
        'confirmPassword',
        'cpf',
        'birthDate',
        'phone',
        'acceptTerms',
        'newsletter'
    ];

    function loadSavedData() {
        const savedData = sessionStorage.getItem(storageKey);
        if (!savedData) return;

        try {
            const formData = JSON.parse(savedData);
            fieldNames.forEach(name => {
                const field = form.querySelector(`#${name}, [name='${name}']`);
                if (!field || !(name in formData)) return;

                if (field.type === 'checkbox') {
                    field.checked = formData[name];
                } else {
                    field.value = formData[name];
                }
            });
        } catch (error) {
            console.warn('Falha ao carregar dados salvos do registro:', error);
        }
    }

    function getFormData() {
        const data = {};
        fieldNames.forEach(name => {
            const field = form.querySelector(`#${name}, [name='${name}']`);
            if (!field) return;
            data[name] = field.type === 'checkbox' ? field.checked : field.value;
        });
        return data;
    }

    function saveFormData() {
        sessionStorage.setItem(storageKey, JSON.stringify(getFormData()));
    }

    function bindField(field) {
        const eventType = field.type === 'checkbox' ? 'change' : 'input';
        field.addEventListener(eventType, saveFormData);
    }

    fieldNames.forEach(name => {
        const field = form.querySelector(`#${name}, [name='${name}']`);
        if (field) bindField(field);
    });

    form.addEventListener('submit', saveFormData);
    loadSavedData();
});
