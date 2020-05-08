import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();
	// retrieve its first Token
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);
	//my code
	Declarations declarations = new Declarations();
	declarations = declarations();
	Block block = new Block();
	block  = statements();
	Program program = new Program(declarations, block);
	//end of my code	
        match(TokenType.RightBrace);
	//my code
	return program;
	//end of my code
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
	//my code
        Declarations ds = new Declarations();

	while(token.type() != TokenType.If && token.type() != TokenType.While && token.type() != TokenType.Identifier && token.type() != TokenType.RightBrace) {
		declaration(ds);
	}

	return ds;
	//end of my code
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
	//my code
	Type dataType = type();

	if(token.type() == TokenType.Int) {
			match(TokenType.Int);	
	} else if(token.type() == TokenType.Bool) {
			match(TokenType.Bool);
	} else if(token.type() == TokenType.Char) {
			match(TokenType.Char);
	} else if(token.type() == TokenType.Float) {
			match(TokenType.Float);
	}

	while(token.type() == TokenType.Identifier)
	{
		if(token.type() == TokenType.Identifier) {
			Variable v = new Variable(token.value());
			Declaration d = new Declaration(v, dataType);
			ds.members.add(d);
			match(TokenType.Identifier);

		} else {
			match(TokenType.Identifier);
		}
		
		if(token.type() == TokenType.Comma) {
			match(TokenType.Comma);
		} else if(token.type() != TokenType.Semicolon ) {
			match(TokenType.Semicolon);
		}

	}

	match(TokenType.Semicolon);
        //end of my code
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
	
        Type t = null;
       	
	if(token.type() == TokenType.Int) {
		t = Type.INT;
	} else if(token.type() == TokenType.Bool ) {
		t = Type.BOOL;
	} else if(token.type() == TokenType.Char) {
		t = Type.CHAR;
	} else if(token.type() == TokenType.Float) {
		t = Type.FLOAT;
	} 

        return t;          
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
        //my code
	if(token.type() == TokenType.Identifier) {
		s = assignment();
	} else if(token.type() == TokenType.If) {
		s = ifStatement();
	} else if(token.type() == TokenType.While) {
		s = whileStatement();
	} else if(token.type() == TokenType.LeftBrace) {
		match(TokenType.LeftBrace);
		s = statements();
	} else if(token.type() == TokenType.Semicolon) {
		match(TokenType.Semicolon);
	}
	//end of my code
	
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
        Block b = new Block();
        //my code
	while(token.type() != TokenType.RightBrace && token.type() != TokenType.Eof)
	{
		Statement currentStatement = statement();
		b.members.add(currentStatement);
	}
	
	//end of my code
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
	//start of my code
	Variable target = new Variable(match(TokenType.Identifier));
	match(TokenType.Assign);
	Expression source = expression();
	match(TokenType.Semicolon);
	return new Assignment(target, source);
	//end of my code
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
	//my code
	match(TokenType.If);
	match(TokenType.LeftParen);
	Expression e = expression();
	match(TokenType.RightParen);

	if(token.type() == TokenType.Semicolon) {
		Statement s = statement();
		Conditional con = new Conditional(e, s);
		return con;
        }

	Statement s = statement();
	match(TokenType.RightBrace);
	
	if(token.type() == TokenType.Else) {
		match(TokenType.Else);
		Statement es = statement();
		Conditional con = new Conditional(e, s, es);
		match(TokenType.RightBrace);
		return con;
	} else {
		Conditional con = new Conditional(e, s);
		return con;
	}

	//end of my code
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
	//my code
	match(TokenType.While);
	match(TokenType.LeftParen);
	Expression e = expression();
	match(TokenType.RightParen);

	if(token.type() == TokenType.Semicolon) {
		Statement s = statement();
		Loop l = new Loop(e, s);	
		return l;
        }

	Statement s = statement();
	Loop l = new Loop(e, s);
	match(TokenType.RightBrace);	

        return l; 
	//end of code	
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
	//my code
	Expression e = conjunction();
	return e;
	//end of my code
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
	//my code
	Expression e = equality();
	while(token.type() == TokenType.Or) {
		Operator op = new Operator(match(TokenType.Or));
		Expression term2 = equality();
		e = new Binary(op, e, term2);
	}
        return e;
	//end of my code
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
	//my code
	Expression e = relation();
	while(token.type() == TokenType.And) {
		Operator op = new Operator(match(TokenType.And));
		Expression term2 = relation();
		e = new Binary(op, e, term2);
	}
        return e;
	//end of my code
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition]
	//my code 
	Expression e = addition();
	while(isRelationalOp() || isEqualityOp()) {
		Operator op = new Operator(match(token.type()));
		Expression term2 = addition();
		e = new Binary(op, e, term2);
	}
	return e;
	//end of my code
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
 
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
	} else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
	//my code
	if(token.type().equals(TokenType.IntLiteral))
	{
		int myVal = Integer.parseInt(token.value());
		IntValue litVal = new IntValue(myVal);
		match(TokenType.IntLiteral);
	       	return litVal;	
	} else if(token.type().equals(TokenType.CharLiteral)) {
		char myVal = (token.value()).charAt(0);
		CharValue litVal = new CharValue(myVal);
		match(TokenType.CharLiteral);
		return litVal;
	} else if(token.type().equals(TokenType.FloatLiteral)) {
		float myVal = Float.parseFloat(token.value());
		FloatValue litVal = new FloatValue(myVal);
		match(TokenType.FloatLiteral);
		return litVal;
	} else if(token.type().equals(TokenType.True)) {
		boolean myVal = true;
		BoolValue litVal = new BoolValue(myVal);
		match(TokenType.True);
		return litVal;
	} else if(token.type().equals(TokenType.False)) {
		boolean myVal = false;
		BoolValue litVal = new BoolValue(myVal);
		match(TokenType.False);
		return litVal;
	} else {
		assert false: "Impossible to reach this point";
		return null;
	}
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser
