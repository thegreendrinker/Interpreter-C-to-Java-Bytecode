// StaticTypeCheck.java

import java.util.*;

// Static type checking for Clite is defined by the functions 
// V and the auxiliary functions typing and typeOf.  These
// functions use the classes in the Abstract Syntax of Clite.


public class StaticTypeCheck {

    public static TypeMap typing (Declarations d) {
        TypeMap map = new TypeMap();
        for (Declaration di : d.members) 
            map.members.put(di.v, di.t);
        return map;
    }

    public static void check(boolean test, String msg) {
        if (test)  return;
        System.err.println(msg);
        System.exit(1);
    }

    public static void V (Declarations d) {
        for (int i=0; i< d.members.size() - 1; i++)
            for (int j=i+1; j <d.members.size(); j++) {
                Declaration di = d.members.get(i);
                Declaration dj = d.members.get(j);
                check( ! (di.v.equals(dj.v)),
                       "duplicate declaration: " + dj.v);
            }
    } 

    public static void V (Program p) {
        V (p.decpart);
        V (p.body, typing (p.decpart));
    } 

    public static Type typeOf (Expression e, TypeMap tm) {
        if (e instanceof Value) return ((Value)e).type;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            check (tm.members.containsKey(v), "undefined variable: " + v);
            return (Type) tm.members.get(v);
        }
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( ))
                if (typeOf(b.term1,tm)== Type.FLOAT)
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            if (u.op.NotOp( ))        return (Type.BOOL);
            else if (u.op.NegateOp( )) return typeOf(u.term,tm);
            else if (u.op.intOp( ))    return (Type.INT);
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
        }
        throw new IllegalArgumentException("should never reach here");
    } 

    public static void V (Expression e, TypeMap tm) {
        if (e instanceof Value) 
            return;
        if (e instanceof Variable) { 
            Variable v = (Variable)e;
            check( tm.members.containsKey(v)
                   , "undeclared variable: " + v);
            return;
        }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Type typ1 = typeOf(b.term1, tm);
            Type typ2 = typeOf(b.term2, tm);
            V (b.term1, tm);
            V (b.term2, tm);
            if (b.op.ArithmeticOp( ))  
                check( typ1 == typ2 &&
                       (typ1 == Type.INT || typ1 == Type.FLOAT)
                       , "type error for " + b.op);
            else if (b.op.RelationalOp( )) 
                check( typ1 == typ2 , "type error for " + b.op);
            else if (b.op.BooleanOp( )) 
                check( typ1 == Type.BOOL && typ2 == Type.BOOL,
                       b.op + ": non-bool operand");
            else
                throw new IllegalArgumentException("should never reach here");
            return;
        }
	//my code
	if(e instanceof Unary) {
		Unary u = (Unary)e;
		V(u.term, tm);
		Type ttype = typeOf(u.term, tm);
		if(u.op.NotOp()) {
			check(ttype == Type.BOOL, "type error for: " + u.op );
		} else if(u.op.NegateOp()) {
			check(ttype == Type.FLOAT || ttype == Type.INT, "type error for: " + u.op);
		} else if(u.op.floatOp() || u.op.charOp()) {
			check(ttype ==  Type.INT, "type error for: " + u.op);
		} else if(u.op.intOp()) {
			check(ttype == Type.FLOAT || ttype == Type.CHAR, "type error for: " + u.op);
		}

		return;
	}
	//end of my code
        throw new IllegalArgumentException("should never reach here");
    }

    public static void V (Statement s, TypeMap tm) {
        if ( s == null )
            throw new IllegalArgumentException( "AST error: null statement");
        if (s instanceof Skip) return;
	//my code
	if(s instanceof Block) {
		Block b = (Block)s;
		for(Statement ss: b.members) {
			V(ss, tm);
		}
		return;
	}
	//end of my code
        if (s instanceof Assignment) {
            Assignment a = (Assignment)s; 
            check( tm.members.containsKey(a.target), " undefined target in assignment: " + a.target);
            V(a.source, tm);
            Type ttype = (Type)tm.members.get(a.target);
            Type srctype = typeOf(a.source, tm);
            if (ttype != srctype) {
                if (ttype == Type.FLOAT)
                    check( srctype == Type.INT
                           , "mixed mode assignment to " + a.target);
                else if (ttype == Type.INT)
                    check( srctype == Type.CHAR
                           , "mixed mode assignment to " + a.target);
                else
                    check( false
                           , "mixed mode assignment to " + a.target);
            }
	    return;
        } 
	if(s instanceof Conditional) {
		Conditional c = (Conditional)s;
		V(c.test, tm);
		Type testtype = typeOf(c.test, tm);
		if(testtype != Type.BOOL) {
			check( false, "test condition: '" + c.strReturn() + "' does not evaluate to a bool type");
		}
		V(c.thenbranch, tm);
		V(c.elsebranch, tm);
		return;
	}
	if(s instanceof Loop) {
		Loop l = (Loop)s;
		V(l.test, tm);
		Type testtype = typeOf(l.test, tm);
		if(testtype != Type.BOOL) {
			check(false, "test condition: '" + l.strReturn() + " 'does not evaluate to a bool type");
		}
		V(l.body, tm);
		return;
	}
        //my code
        throw new IllegalArgumentException("should never reach here");
    }

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display(); 
        System.out.println("\nBegin type checking...");
        System.out.println("Type map:");
        TypeMap map = typing(prog.decpart);
        map.display();   
        V(prog);
    } //main

} // class StaticTypeCheck
