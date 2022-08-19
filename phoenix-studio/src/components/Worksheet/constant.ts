interface Completion {
  label: string,
  detail: string
}

/**
 * SQL 编辑框关键字提示
 */
export const SQL_STR: Completion[] = [
  {
    'label': 'ADD',
    'detail': 'Adds a column in an existing table',
  },
  {
    'label': 'ADD CONSTRAINT',
    'detail': 'Adds a constraint after a table is already created',
  },
  {
    'label': 'ALTER',
    'detail': 'Adds, deletes, or modifies columns in a table, or changes the data \ntype of a column in a table',
  },
  {
    'label': 'ALTER COLUMN',
    'detail': 'Changes the data type of a column in a table',
  },
  {
    'label': 'ALTER TABLE',
    'detail': 'Adds, deletes, or modifies columns in a table',
  },
  {
    'label': 'ALL',
    'detail': 'Returns true if all of the subquery values meet the \ncondition',
  },
  {
    'label': 'AND',
    'detail': 'Only includes rows where both conditions is true',
  },
  {
    'label': 'ANY',
    'detail': 'Returns true if any of the subquery values meet the \ncondition',
  },
  {
    'label': 'AS',
    'detail': 'Renames a column or table with an alias',
  },
  {
    'label': 'ASC',
    'detail': 'Sorts the result set in ascending order',
  },
  {
    'label': 'BACKUP DATABASE',
    'detail': 'Creates a back up of an existing database',
  },
  {
    'label': 'BETWEEN',
    'detail': 'Selects values within a given range',
  },
  {
    'label': 'CASE',
    'detail': 'Creates different outputs based on conditions',
  },
  {
    'label': 'CHECK',
    'detail': 'A constraint that limits the value that can be placed in a \ncolumn',
  },
  {
    'label': 'COLUMN',
    'detail': 'Changes the data type of a column or deletes a column in a table',
  },
  {
    'label': 'CONSTRAINT',
    'detail': 'Adds or deletes a constraint',
  },
  {
    'label': 'CREATE',
    'detail': 'Creates a database, index, view, table, or procedure',
  },
  {
    'label': 'CREATE DATABASE',
    'detail': 'Creates a new SQL database',
  },
  {
    'label': 'CREATE INDEX',
    'detail': 'Creates an index on a table (allows duplicate values)',
  },
  {
    'label': 'CREATE OR REPLACE VIEW',
    'detail': 'Updates a view',
  },
  {
    'label': 'CREATE TABLE',
    'detail': 'Creates a new table in the database',
  },
  {
    'label': 'CREATE PROCEDURE',
    'detail': 'Creates a stored procedure',
  },
  {
    'label': 'CREATE UNIQUE INDEX',
    'detail': 'Creates a unique index on a table (no duplicate values)',
  },
  {
    'label': 'CREATE VIEW',
    'detail': 'Creates a view based on the result set of a SELECT statement',
  },
  {
    'label': 'DATABASE',
    'detail': 'Creates or deletes an SQL database',
  },
  {
    'label': 'DEFAULT',
    'detail': 'A constraint that provides a default value for a column',
  },
  {
    'label': 'DELETE',
    'detail': 'Deletes rows from a table',
  },
  {
    'label': 'DESC',
    'detail': 'Sorts the result set in descending order',
  },
  {
    'label': 'DISTINCT',
    'detail': 'Selects only distinct (different) values',
  },
  {
    'label': 'DROP',
    'detail': 'Deletes a column, constraint, database, index, table, or view',
  },
  {
    'label': 'DROP COLUMN',
    'detail': 'Deletes a column in a table',
  },
  {
    'label': 'DROP CONSTRAINT',
    'detail': 'Deletes a UNIQUE, PRIMARY KEY, FOREIGN KEY, or CHECK constraint',
  },
  {
    'label': 'DROP DATABASE',
    'detail': 'Deletes an existing SQL database',
  },
  {
    'label': 'DROP DEFAULT',
    'detail': 'Deletes a DEFAULT constraint',
  },
  {
    'label': 'DROP INDEX',
    'detail': 'Deletes an index in a table',
  },
  {
    'label': 'DROP TABLE',
    'detail': 'Deletes an existing table in the database',
  },
  {
    'label': 'DROP VIEW',
    'detail': 'Deletes a view',
  },
  {
    'label': 'EXEC',
    'detail': 'Executes a stored procedure',
  },
  {
    'label': 'EXISTS',
    'detail': 'Tests for the existence of any record in a subquery',
  },
  {
    'label': 'FOREIGN KEY',
    'detail': 'A constraint that is a key used to link two tables together',
  },
  {
    'label': 'FROM',
    'detail': 'Specifies which table to select or delete data from',
  },
  {
    'label': 'FULL OUTER JOIN',
    'detail': 'Returns all rows when there is a match in either left table or right table',
  },
  {
    'label': 'GROUP BY',
    'detail': 'Groups the result set (used with aggregate functions: COUNT, MAX, MIN, SUM, \nAVG)',
  },
  {
    'label': 'HAVING',
    'detail': 'Used instead of WHERE with aggregate functions',
  },
  {
    'label': 'IN',
    'detail': 'Allows you to specify multiple values in a WHERE clause',
  },
  {
    'label': 'INDEX',
    'detail': 'Creates or deletes an index in a table ',
  },
  {
    'label': 'INNER JOIN',
    'detail': 'Returns rows that have matching values in both tables',
  },
  {
    'label': 'INSERT INTO',
    'detail': 'Inserts new rows in a table',
  },
  {
    'label': 'INSERT INTO SELECT',
    'detail': 'Copies data from one table into another table',
  },
  {
    'label': 'IS NULL',
    'detail': 'Tests for empty values',
  },
  {
    'label': 'IS NOT NULL',
    'detail': 'Tests for non-empty values',
  },
  {
    'label': 'JOIN',
    'detail': 'Joins tables',
  },
  {
    'label': 'LEFT JOIN',
    'detail': 'Returns all rows from the left table, and the matching rows from the right \ntable',
  },
  {
    'label': 'LIKE',
    'detail': 'Searches for a specified pattern in a column',
  },
  {
    'label': 'LIMIT',
    'detail': 'Specifies the number of records to return in the result set',
  },
  {
    'label': 'NOT',
    'detail': 'Only includes rows where a condition is not true',
  },
  {
    'label': 'NOT NULL',
    'detail': 'A constraint that enforces a column to not accept NULL values',
  },
  {
    'label': 'OR',
    'detail': 'Includes rows where either condition is true',
  },
  {
    'label': 'ORDER BY',
    'detail': 'Sorts the result set in ascending or descending order',
  },
  {
    'label': 'OUTER JOIN',
    'detail': 'Returns all rows when there is a match in either left table or right table',
  },
  {
    'label': 'PRIMARY KEY',
    'detail': 'A constraint that uniquely identifies each record in a database table',
  },
  {
    'label': 'PROCEDURE',
    'detail': 'A stored procedure',
  },
  {
    'label': 'RIGHT JOIN',
    'detail': 'Returns all rows from the right table, and the matching rows from the \nleft table',
  },
  {
    'label': 'ROWNUM',
    'detail': 'Specifies the number of records to return in the result set',
  },
  {
    'label': 'SELECT',
    'detail': 'Selects data from a database',
  },
  {
    'label': 'SELECT DISTINCT',
    'detail': 'Selects only distinct (different) values',
  },
  {
    'label': 'SELECT INTO',
    'detail': 'Copies data from one table into a new table',
  },
  {
    'label': 'SELECT TOP',
    'detail': 'Specifies the number of records to return in the result set',
  },
  {
    'label': 'SET',
    'detail': 'Specifies which columns and values that should be updated in a table',
  },
  {
    'label': 'TABLE',
    'detail': 'Creates a table, or adds, deletes, or modifies columns in a table, or \ndeletes a table or data inside a table',
  },
  {
    'label': 'TOP',
    'detail': 'Specifies the number of records to return in the result set',
  },
  {
    'label': 'TRUNCATE TABLE',
    'detail': 'Deletes the data inside a table, but not the table itself',
  },
  {
    'label': 'UNION',
    'detail': 'Combines the result set of two or more SELECT statements (only \ndistinct values)',
  },
  {
    'label': 'UNION ALL',
    'detail': 'Combines the result set of two or more SELECT statements (allows \nduplicate values)',
  },
  {
    'label': 'UNIQUE',
    'detail': 'A constraint that ensures that all values in a column are unique',
  },
  {
    'label': 'UPDATE',
    'detail': 'Updates existing rows in a table',
  },
  {
    'label': 'VALUES',
    'detail': 'Specifies the values of an INSERT INTO statement',
  },
  {
    'label': 'VIEW',
    'detail': 'Creates, updates, or deletes a view',
  },
  {
    'label': 'WHERE',
    'detail': 'Filters a result set to include only records that fulfill a specified \ncondition',
  },
]
