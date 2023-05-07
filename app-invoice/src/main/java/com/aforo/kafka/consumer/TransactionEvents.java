package com.aforo.kafka.consumer;

import com.aforo.dao.InvoiceDao;
import com.aforo.model.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionEvents {

    @Autowired
    private InvoiceDao _dao;

    @Autowired
    private ObjectMapper objectMapper;

    private Logger log = LoggerFactory.getLogger(TransactionEvents.class);

    public void processTransactionEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        Invoice event = objectMapper.readValue(consumerRecord.value(), Invoice.class);
        log.info("Actulizando Invoice ***" + event.getIdInvoice());
        event.setState(1);
   		log.info("Se ha pagado la factura # " + event.getIdInvoice());

        this.updateInvoicePayment(event);
        //_dao.save(event);
    }

    private void updateInvoicePayment(Invoice invoice){
        Optional<Invoice> currentInvoice = _dao.findById(invoice.getIdInvoice());

        if(currentInvoice.isPresent()){
            Invoice updatedInvoice = currentInvoice.get();
            updatedInvoice.setAmount(updatedInvoice.getAmount()-invoice.getAmount());
            _dao.save(updatedInvoice);
        }
        else{
            _dao.save(invoice);
        }
    }
}
