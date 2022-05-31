package pt.unl.fct.di.adc.silvanus.util.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import pt.unl.fct.di.adc.silvanus.data.parcel.Coordenada;
import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public interface Parcel {

    /**
     * Este metodo e o metodo usado para enviar uma parcela (caso nao haja falhas) para a lista de espera das parcelas por aprovar.
	 * Existem as seguintes formas de a criacao de uma parcela falhar:
	 *     <p>- a parcela nao esta completamente contida na Big Bounding Box (Bounding Box composta pelos extremos da latitude e da longitude)</p>
	 *     - se a parcela interseta uma ilha mas nao esta completamente dentro da Bounding Box da mesma
	 *     <p>- se a parcela nao esta completamente dentro da bounding box de Portugal (ou seja, apanha "agua")</p>
     *
     * @param dataParcela os dados necessarios para criar a parcela, exceto documentos. Esses documentos sao inseridos atraves de um outro pedido
     * @return ok caso tenha tudo corrido bem, um erro caso contrario
     */
    Result<Void> createParcel(ParcelaData dataParcela) throws JsonProcessingException;


	void quuéééééééééééééééééééééériiiiiiiiiisssssssssss(Coordenada[] parcela);
}
