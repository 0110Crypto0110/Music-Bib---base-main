package exception;

// estende RuntimeException ou Exception (obrigada a tratar)
public class RegraNegocioException extends RuntimeException {
    
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}