// Gerenciador de Biblioteca Pessoal - JavaScript

async function buscarIsbn() {
    const isbnInput = document.getElementById('isbn');
    const isbn = isbnInput ? isbnInput.value.trim() : '';
    if (!isbn) { alert('Digite um ISBN primeiro.'); return; }

    try {
        const resp = await fetch('/api/open-library/isbn/' + isbn);
        if (!resp.ok) { alert('Livro não encontrado para este ISBN.'); return; }
        const data = await resp.json();

        if (data.titulo) document.getElementById('titulo').value = data.titulo;
        if (data.autor) document.getElementById('autor').value = data.autor;
        if (data.editora) document.getElementById('editora').value = data.editora;
        if (data.anoPublicacao) document.getElementById('anoPublicacao').value = data.anoPublicacao;
        alert('Dados preenchidos com sucesso!');
    } catch (e) {
        alert('Erro ao buscar informações do livro.');
    }
}

// Auto-dismiss alerts
document.addEventListener('DOMContentLoaded', () => {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(a => setTimeout(() => a.style.display = 'none', 4000));
});
