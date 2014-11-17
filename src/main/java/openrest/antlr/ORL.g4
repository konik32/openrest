grammar ORL;

@members {
	StringBuilder buf = new StringBuilder();
}

functionName
:
	FUNCTION_NAME
;

function
:
	functionName LPAREN propertyName
	(
		',' parameter
	)* RPAREN
;

propertyName
:
	PROPERTY
;

parameter
:
	NUMBER # parameterNumberValue
	| TEXT # parameterTextValue
;

logicalExpression
:
	logicalExpression AND logicalExpression # logicalExpressionAnd
	| logicalExpression OR logicalExpression # logicalExpressionOr
	| LPAREN logicalExpression RPAREN # logicalExpressionNested
	| function # logicalExpressionAtom
;

OR
:
	';or;'
;

AND
:
	';and;'
;

LPAREN
:
	'('
;

RPAREN
:
	')'
;

FUNCTION_NAME
:
	(
		'between'
		| 'isNotNull'
		| 'isNull'
		| 'lt'
		| 'gt'
		| 'ge'
		| 'le'
		| 'before'
		| 'after'
		| 'notLike'
		| 'like'
		| 'statingWith'
		| 'endingWith'
		| 'containing'
		| 'notIn'
		| 'in'
		| 'true'
		| 'false'
		| 'eq'
	)
;

PROPERTY
:
	(
		'a' .. 'z'
		| 'A' .. 'Z'
		| '_'
	)
	(
		'a' .. 'z'
		| 'A' .. 'Z'
		| '0' .. '9'
		| '_'
		| '$'
		| '.'
	)*
;
WHITESPACE: (' ' | '\t')+ -> skip;

TEXT
:
	'\''
	(
		'~' '\''
		{buf.append('\'');}

		| '~~'
		{buf.append('~');}

		| ~( '~' | '\'' )
		{buf.append((char)_input.LA(-1));}

	)* '\''
;

NUMBER
:
	(
		INT
		| FLOAT
	)
;

FLOAT
:
	INT '.' INT
;

INT
:
	(
		'0' .. '9'
	)+
;



