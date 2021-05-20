package italo.banking.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErroResponse {

	public final static int INVALID_DATE_FORMAT = 1;

	public final static int ACCOUNT_NOT_FOUND = 100;
	public final static int SOURCE_ACCOUNT_NOT_FOUND = 101;
	public final static int DEST_ACCOUNT_NOT_FOUND = 102;	
	public final static int HOLDER_ALREADY_EXISTS = 103;
	public final static int CREDIT_NOT_ENOUGH = 104;
	public final static int DATE_BEFORE_SCHEDULING_DATE = 105;
		
	private int codigo;
	private String message;
	
	public ErroResponse( int codigo ) {
		this.codigo = codigo;
		switch( codigo ) {
			case INVALID_DATE_FORMAT: 
				message = "Data em formato inválido.";
				break;
			case ACCOUNT_NOT_FOUND:
				message = "Conta não encontrada.";
				break;
			case SOURCE_ACCOUNT_NOT_FOUND:
				message = "Conta de origem não encontrada.";
				break;
			case DEST_ACCOUNT_NOT_FOUND:
				message = "Conta de destino não encontrada.";
				break;
			case HOLDER_ALREADY_EXISTS:
				message = "Já existe um titular registrado com o nome informado.";
				break;
			case CREDIT_NOT_ENOUGH:
				message = "Tentativa de debitar um valor maior que o disponível na conta.";
				break;
			case DATE_BEFORE_SCHEDULING_DATE:
				message = "Data de operação anterior a data de agendamento. Deve ser igual ou posterior!";
				break;
			default: message = "Erro desconhecido.";
		}
	}
	
}
