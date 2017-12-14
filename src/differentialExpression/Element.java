package differentialExpression;

class Element implements Comparable<Element> {

    int index;
    double value;

    Element(int index, double value){
        this.index = index;
        this.value = value;
    }

    public int compareTo(Element e) {
        return Double.compare(this.value, e.value);
    }
}