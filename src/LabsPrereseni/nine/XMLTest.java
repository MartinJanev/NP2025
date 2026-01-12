package LabsPrereseni.nine;


import java.util.*;


interface XMLComponent {
    void addAttribute(String key, String value);
}

class XMLLeaf implements XMLComponent {

    private String key, value;
    private Map<String, String> attributes;

    public XMLLeaf(String key, String value) {
        this.key = key;
        this.value = value;
        this.attributes = new TreeMap<>(Comparator.reverseOrder());
    }

    @Override
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + key);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        sb.append(">" + value + "</" + key + ">");
        return sb.toString();
    }
}

class XMLComposite implements XMLComponent {
    private String tag;
    private List<XMLComponent> components;
    private Map<String, String> attributes;

    public XMLComposite(String tag) {
        this.tag = tag;
        this.components = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    @Override
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void addComponent(XMLComponent component) {
        components.add(component);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + tag);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        sb.append(">\n");

        for (XMLComponent component : components) {
            String componenta = component.toString();
            String [] perLine = componenta.split("\n");
            for (String s : perLine) {
                sb.append("    " + s  + "\n");
            }
        }
        sb.append("</" + tag + ">");
        return sb.toString();
    }
}


public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase == 1) {
            //TODO Print the component object
            System.out.println(component);
        } else if (testCase == 2) {
            //TODO print the composite object
            System.out.println(composite);
        } else if (testCase == 3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level", "1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level", "2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level", "3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            //TODO print the main object
            System.out.println(main);
        }
    }
}
