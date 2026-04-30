document.addEventListener('DOMContentLoaded', function () {
    const cpfInput = document.getElementById('cpf');
    if (!cpfInput) return;

    const cleanCPF = (value) => value.replace(/\D+/g, '').slice(0, 11);

    const formatCPF = (value) => {
        const digits = cleanCPF(value);
        if (!digits) return '';

        let formatted = digits;
        if (digits.length > 3) {
            formatted = `${digits.slice(0, 3)}.${digits.slice(3)}`;
        }
        if (digits.length > 6) {
            formatted = `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`;
        }
        if (digits.length > 9) {
            formatted = `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
        }
        return formatted;
    };

    const applyFormat = () => {
        const cursorPosition = cpfInput.selectionStart;
        const prevValue = cpfInput.value;
        const formattedValue = formatCPF(prevValue);

        cpfInput.value = formattedValue;

        if (cursorPosition !== null) {
            const digitCountBeforeCursor = cleanCPF(prevValue.slice(0, cursorPosition)).length;
            let newCursor = digitCountBeforeCursor;
            if (digitCountBeforeCursor > 3) newCursor += 1;
            if (digitCountBeforeCursor > 6) newCursor += 1;
            if (digitCountBeforeCursor > 9) newCursor += 1;
            cpfInput.setSelectionRange(newCursor, newCursor);
        }
    };

    cpfInput.addEventListener('input', applyFormat);

    const form = cpfInput.closest('form');
    if (form) {
        form.addEventListener('submit', function () {
            cpfInput.value = cleanCPF(cpfInput.value);
        });
    }

    if (cpfInput.value) {
        cpfInput.value = formatCPF(cpfInput.value);
    }
});
