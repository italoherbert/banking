package italo.banking.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "Objeto de requisição de registro de transação")
public class TransactionRequest {
		
	private double value;
	
	@ApiModelProperty(name = "data de agendamento", example = "19/05/2021")
	private String operationDate;
			
}
