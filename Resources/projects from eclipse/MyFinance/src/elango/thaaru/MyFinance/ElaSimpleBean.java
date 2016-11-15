package elango.thaaru.MyFinance;

import elango.thaaru.MyFinance.R.string;


public class ElaSimpleBean {
	private int int_val1 = G.NOTHING_INT;
	private int int_val2 = G.NOTHING_INT;
	private String str_val1 = G.NOTHING_STR;
	private String str_val2 = G.NOTHING_STR;	

	public ElaSimpleBean(int int_val1, String str_val1) {
		this.int_val1 = int_val1;
		this.str_val1 = str_val1;
	}

	public ElaSimpleBean(int int_val1, String str_val1, String str_val2) {
		this.int_val1 = int_val1;
		this.str_val1 = str_val1;
		this.str_val2 = str_val2;
	}

	public int getIntVal1() {
		return int_val1;
	}

	public void setIntVal1(int int_val1) {
		this.int_val1 = int_val1;
	}
	
	public int getIntVal2() {
		return int_val2;
	}

	public void setIntVal2(int attribute1) {
		this.int_val2 = attribute1;
	}

	public String getStrVal1() {
		return str_val1;
	}

	public void setStrVal1(String str_val1) {
		this.str_val1 = str_val1;
	}

	public String getStrVal2() {
		return str_val2;
	}

	public void setStrVal2(String str_val2) {
		this.str_val2 = str_val2;
	}

	public String toString() {
		return this.str_val1;
	}

}
