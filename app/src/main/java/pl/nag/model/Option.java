package pl.nag.model;

public class Option {
    private String text;
    private String value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getValue() {
        return value != null ? Double.parseDouble(value) : 0;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Option{" +
                "text='" + text + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
