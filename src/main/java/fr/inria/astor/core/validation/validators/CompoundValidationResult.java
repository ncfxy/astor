package fr.inria.astor.core.validation.validators;

import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.entities.ProgramVariantValidationResult;
/**
 * Contains a set of Validation Results
 * @author Matias Martinez
 *
 */
public class CompoundValidationResult extends ProgramVariantValidationResult {

	protected Map<String,ProgramVariantValidationResult> validations = new HashMap<>();
	
	public CompoundValidationResult() {
		
	}
	

	public void addValidation(String mode, ProgramVariantValidationResult p){
		this.validations.put(mode, p);
	}
	
	
	public  ProgramVariantValidationResult getValidation(String mode){
		return this.validations.get(mode);
	}

	@Override
	public boolean wasSuccessful() {
		for (ProgramVariantValidationResult  pv : this.validations.values()) {
			if(!pv.wasSuccessful()){
				return false;
			}
		}
		return true;
	};
	
	
	public String toString(){
		String r = "";
		for(String mode: this.validations.keySet()){
			
				r+= "\n"+mode+": "+ this.getValidation(mode).toString();
			}
		return r;
	}


	@Override
	public int getFailureCount() {
		int count = 0;
		for (ProgramVariantValidationResult  pv : this.validations.values()) {
			count+= pv.getFailureCount();
		}
		
		return count;
	}


	@Override
	public boolean isRegressionExecuted() {
		
		return false;
	}


	@Override
	public void setRegressionExecuted(boolean regressionExecuted) {
	
		
	}


	@Override
	public int getPassingTestCases() {
		int count = 0;
		for (ProgramVariantValidationResult  pv : this.validations.values()) {
			count+= pv.getPassingTestCases();
		}
		
		return count;
	}
	
	@Override
	public int getCasesExecuted() {
		
		return getPassingTestCases() + getFailureCount();
	}
	

}
