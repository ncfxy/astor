package fr.inria.main.evolution;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.core.faultlocalization.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.JGenProg;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.UniformRandomFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.LoopExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.setup.MutationProperties;
import fr.inria.astor.core.setup.MutationSupporter;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;

/**
 * Main for full version of jGenProg
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MainjGenProg extends AbstractMain{
	



@Override
public void run(String location, String projectName, String dependencies, Stats currentStat, String packageToMine)
		throws Exception {
	
	
}

@Override
public void run(String location, String projectName, String dependencies, Stats currentStat, String packageToInstrument,
		double thfl, String failing) throws Exception {
	
	//System.out.println(System.getProperty("java.class.path"));
	if(thfl>0)
		MutationProperties.THRESHOLD_SUSPECTNESS = thfl;
	
	List<String> failingList = Arrays.asList(new String[] { failing });
	String method = this.getClass().getSimpleName();
	rep = getProject(location, projectName,method , failing, failingList,dependencies,true);
	rep.getProperties().setExperimentName(this.getClass().getSimpleName());
			
	rep.init(MutationSupporter.DEFAULT_ORIGINAL_VARIANT);
	
	
	MutationSupporter mutSupporter = new MutationSupporter(getFactory());
	JGenProg gploop = new JGenProg(mutSupporter,rep);
	gploop.setCurrentStat(currentStat);
			
	//Fix Space
	List<AbstractFixSpaceProcessor> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor>();
	
	ingredientProcessors.add(new SingleStatementFixSpaceProcessor());
	ingredientProcessors.add(new LoopExpressionFixSpaceProcessor());
	ingredientProcessors.add(new IFExpressionFixSpaceProcessor());
	
	
	gploop.setFixspace(new UniformRandomFixSpace(ingredientProcessors));
	//---
	gploop.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));
	//--
	
	//Repair Space
	gploop.setRepairSpace(new UniformRandomRepairOperatorSpace());
	
	//Pop controller
	gploop.setPopulationControler(new FitnessPopulationController());
	

	
	//Suspicious
	List<SuspiciousCode> candidates = rep.getSuspicious(
			packageToInstrument,
			MutationSupporter.DEFAULT_ORIGINAL_VARIANT);
	List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

	for (SuspiciousCode suspiciousCode : candidates) {
		if(!suspiciousCode.getClassName().endsWith("Exception") 
							){
			filtercandidates.add(suspiciousCode);
		}
	}
	currentStat.fl_size = filtercandidates.size();
	currentStat.fl_threshold = MutationProperties.THRESHOLD_SUSPECTNESS ;
	
	assertNotNull(candidates);
	assertTrue(candidates.size() > 0);
	try {
		gploop.start(filtercandidates);
	} catch (Exception e) {
		e.printStackTrace();
		fail(e.getMessage());
	}
}

}
