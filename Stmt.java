package himluck;

abstract class Stmt {
    interface Visitor<R> {
        R visitPrintStmt(Print stmt);

        R visitExpressionStmt(Expression stmt);
        // 나중에 더 많은 Stmt 유형을 처리할 수 있도록 추가 가능
    }

    // expression 문장 처리
    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }

    // print 문장 처리
    static class Print extends Stmt {
        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
