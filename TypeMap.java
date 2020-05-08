import java.util.*;

public class TypeMap extends HashMap<Variable, Type> {
	//TypeMap is implemented as a Java HashMap.
	//Plus a 'display' method to facilitate experimentation
	//my code
	public HashMap<Variable, Type> members = new HashMap<Variable, Type>();

	void display() {
		System.out.print("{ ");
		for(Variable v : members.keySet()) {
			System.out.print("<");
			v.display();
			System.out.print(", ");
			(members.get(v)).display();
			System.out.print("> ");
		}
		System.out.println("}");
	}	
	//end of my code
}
