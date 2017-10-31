package plots;

public abstract class Plot {
	
	String xlab;
	String ylab;
	String title;
	void setTitle(String t) {title = t;}
	void setXlab(String x) {xlab = x;}
	void setYlab(String y) {ylab = y;}
	abstract String generateCommand();

}
