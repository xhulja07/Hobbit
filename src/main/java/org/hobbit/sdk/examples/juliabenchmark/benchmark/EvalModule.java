package org.hobbit.sdk.examples.juliabenchmark.benchmark;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.hobbit.core.components.AbstractEvaluationModule;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.vocab.HOBBIT;
import org.hobbit.sdk.examples.juliabenchmark.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.ParserFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.hobbit.core.components.AbstractEvaluationModule;
import org.hobbit.vocab.HOBBIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EvalModule extends AbstractEvaluationModule {
    private static final Logger logger = LoggerFactory.getLogger(EvalModule.class);
    public int nrData = 0 ;
    public int hitForHeaD;
    public int hitForTail;
    HitAtVal h1;
    HitAtVal h3;
    HitAtVal h10;

//map 
    @Override
    protected void evaluateResponse(byte[] expectedData, byte[] receivedData, long taskSentTimestamp, long responseReceivedTimestamp) throws Exception {
        // evaluate the given response and store the result, e.g., increment internal counters
    	nrData ++;
    	//for hit@3 
    	h1 = new HitAtVal(1);
    	h3 = new HitAtVal(3);
    	h10 = new HitAtVal(10);
    	convertData(receivedData);
    	h1.increasetHitHead(hitForHeaD);
    	h3.increasetHitHead(hitForHeaD);
    	h10.increasetHitHead(hitForHeaD);
    	h1.increasetHitTail(hitForTail);
    	h3.increasetHitTail(hitForTail);
    	h10.increasetHitTail(hitForTail);
        logger.trace("evaluateResponse()");
    }

    @Override
    protected Model summarizeEvaluation() throws Exception {
        logger.debug("summarizeEvaluation()");
        // All tasks/responsens have been evaluated. Summarize the results,
        // write them into a Jena model and send it to the benchmark controller.
        h1.getAccuracyHead(nrData);
        h1.getAccuracyTail(nrData);
        h3.getAccuracyHead(nrData);
        h3.getAccuracyTail(nrData);
        h10.getAccuracyHead(nrData);
        h10.getAccuracyTail(nrData);
        Model model = createDefaultModel();
        Resource experimentResource = model.getResource(experimentUri);
        model.add(experimentResource , RDF.type, HOBBIT.Experiment);
        model.add(experimentResource, model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), model.getResource("http://w3id.org/hobbit/vocab#Experiment"));
        model.add(experimentResource, model.getProperty(Constants.KPI_HIT_AT_1), model.createTypedLiteral(h1));

        logger.debug(model.toString());


        logger.debug("Sending result model: {}", RabbitMQUtils.writeModel2String(model));

        return model;
    }
    
    

    @Override
    public void close(){
        // Free the resources you requested here
        logger.debug("close()");
        // Always close the super class after yours!
        try {
            super.close();
        }
        catch (Exception e){

        }
    }
    
    public void convertData(byte[] data) {
    	String rData[]= data.toString().split(" ");
    	hitForHeaD = Integer.parseInt(rData[0]);
    	hitForTail = Integer.parseInt(rData[1]);
    	
    }
    
}
