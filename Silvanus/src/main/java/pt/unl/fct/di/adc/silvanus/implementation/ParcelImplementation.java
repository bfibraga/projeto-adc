package pt.unl.fct.di.adc.silvanus.implementation;

import java.util.logging.Logger;

import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.silvanus.data.parcel.ParcelaData;
import pt.unl.fct.di.adc.silvanus.resources.ParcelaResource;
import pt.unl.fct.di.adc.silvanus.util.interfaces.Parcel;
import pt.unl.fct.di.adc.silvanus.util.result.Result;

public class ParcelImplementation implements Parcel {

	private static final Logger LOG = Logger.getLogger(ParcelaResource.class.getName());

	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	@Override
	public Result<Void> createParcel(ParcelaData dataParcela) {
		LOG.fine("Attempt to register parcela by user: " + dataParcela.getId_of_owner());

		String parcelID = dataParcela.getId_of_owner() + "/" + dataParcela.getName_of_terrain();
		Key parcelaKey = datastore.newKeyFactory().setKind("Parcela")
				.newKey(parcelID);
		
		Entity parcelaEntity = datastore.get(parcelaKey);
		if (parcelaEntity != null)
			return Result.error(Status.FORBIDDEN, "Already exists this parcel");
		
		Transaction txn = datastore.newTransaction();
		try {
			parcelaEntity = Entity.newBuilder(parcelaKey).set("polygon", dataParcela.getParcela())
					.set("id_owner", dataParcela.getId_of_owner())
					.set("name_of_terrain", dataParcela.getName_of_terrain())
					.set("description_of_terrain", dataParcela.getDescription_of_terrain())
					.set("conselho_of_terrain", dataParcela.getConselho_of_terrain())
					.set("freguesia_of_concelho", dataParcela.getFreguesia_of_terrain())
					.set("section_of_terrain", dataParcela.getFreguesia_of_terrain())
					.set("section_of_terrain", dataParcela.getSection_of_terrain())
					.set("number_article_of_terrain", dataParcela.getNumber_article_terrain())
					.set("verification_document_of_terrain", dataParcela.getVerification_document_of_terrain())
					.set("type_of_soil_coverage", dataParcela.getType_of_soil_coverage())
					.set("current_use_of_soil", dataParcela.getCurrent_use_of_soil())
					.set("previous_use_of_soil", dataParcela.getPrevious_use_of_soil()).build();
			txn.add(parcelaEntity);
			txn.commit();
			LOG.info("Parcela was registered.");
			return Result.ok();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

}
