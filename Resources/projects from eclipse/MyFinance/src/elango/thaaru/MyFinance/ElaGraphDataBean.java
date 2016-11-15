package elango.thaaru.MyFinance;

import java.util.LinkedHashMap;
import java.util.Map;

public class ElaGraphDataBean {
	private int id;
	private String value;
	private String description;
	private Map<Integer, Float> dataMap = new LinkedHashMap<Integer, Float>();
	private int color;

	public ElaGraphDataBean(int id, String value) {
		this.id = id;
		this.value = value;
		this.description = G.NOTHING_STR;
	}

	public ElaGraphDataBean(int id, String value, String description) {
		this.id = id;
		this.value = value;
		this.description = description;
	}
	
	public ElaGraphDataBean(int id, Map<Integer, Float> dataMap,int color) {
		this.id = id;
		this.dataMap = dataMap;
		this.color = color;
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Map<Integer, Float> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<Integer, Float> dataMap) {
		this.dataMap = dataMap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return value;
	}

}
