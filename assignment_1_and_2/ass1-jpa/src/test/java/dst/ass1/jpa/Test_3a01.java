package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.ModelFactory;

public class Test_3a01 {
	
	private ModelFactory modelFactory = new ModelFactory();

	@Test
	public void testBuiltInConstaintsInvalid() {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		Validator val = vf.getValidator();

		IComputer computer = modelFactory.createComputer();
		computer.setName("comp");
		computer.setCpus(5);
		computer.setLocation("location");
		computer.setCreation(createDate(2012, 01, 01));
		computer.setLastUpdate(createDate(2012, 01, 01));

		Set<ConstraintViolation<IComputer>> violation = val.validate(computer);
		assertNotNull(violation);
		assertEquals(2, violation.size());	
	}
	
	@Test
	public void testBuiltInConstaintsValid() {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		Validator val = vf.getValidator();

		IComputer computer = modelFactory.createComputer();
		computer.setName("computer");
		computer.setCpus(5);
		computer.setLocation("AUT-VIE@1040");
		computer.setCreation(createDate(2011,01, 01));
		computer.setLastUpdate(createDate(2011, 01, 01));
		

		Set<ConstraintViolation<IComputer>> violation = val.validate(computer);
		assertEquals(0, violation.size());

	}

	
	private Date createDate(int year, int month, int day) {

		String temp = year + "/" + month + "/" + day;
		Date date = null;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
			date = formatter.parse(temp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;

	}	
}
