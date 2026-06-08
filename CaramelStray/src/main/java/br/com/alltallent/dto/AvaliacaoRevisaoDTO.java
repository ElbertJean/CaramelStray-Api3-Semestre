package br.com.alltallent.dto;

import br.com.alltallent.model.Avaliacao;
import br.com.alltallent.model.AvaliacaoFuncionario;
import br.com.alltallent.model.RespostaColaborador;

import java.util.Collections;
import java.util.List;

public record AvaliacaoRevisaoDTO(
        Long avaliacaoFuncionarioCodigo,
        String nomeFuncionario,
        String tituloAvaliacao,
        String comentarioColaborador,
        String statusAtual,
        List<PerguntaComRespostaDTO> perguntasComRespostas) {
    public AvaliacaoRevisaoDTO(AvaliacaoFuncionario instancia, Avaliacao avaliacaoBase) {
        this(
                instancia.getCodigo(),
                (instancia.getFuncionario() != null) ? instancia.getFuncionario().getNomeCompleto() : null,
                avaliacaoBase.getTitulo(),
                instancia.getComentarioColaborador(),
                instancia.getResultadoStatus(),
                (avaliacaoBase.getPerguntas() != null) ? avaliacaoBase.getPerguntas().stream()
                        .map(pergunta -> new PerguntaComRespostaDTO(pergunta, obterRespostas(instancia)))
                        .toList()
                        : Collections.emptyList());
    }

    private static List<RespostaColaborador> obterRespostas(AvaliacaoFuncionario instancia) {
        return instancia.getRespostas() != null ? List.copyOf(instancia.getRespostas()) : Collections.emptyList();
    }
}