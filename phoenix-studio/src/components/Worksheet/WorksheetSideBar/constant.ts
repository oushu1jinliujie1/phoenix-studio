/**
 * 列类型
 */
export const TYPE_OPTION_LIST = [
  {
    label: 'BIGINT',
    value: 'BIGINT',
  }, {
    label: 'VARCHAR',
    value: 'VARCHAR',
  }, {
    label: 'UNSIGNED_INT',
    value: 'UNSIGNED_INT',
  }, {
    label: 'UNSIGNED_LONG',
    value: 'UNSIGNED_LONG',
  }, {
    label: 'INTEGER',
    value: 'INTEGER',
  }, {
    label: 'TINYINT',
    value: 'TINYINT',
  }, {
    label: 'UNSIGNED_TINYINT',
    value: 'UNSIGNED_TINYINT',
  }, {
    label: 'SMALLINT',
    value: 'SMALLINT',
  }, {
    label: 'UNSIGNED_SMALLINT',
    value: 'UNSIGNED_SMALLINT',
  }, {
    label: 'FLOAT',
    value: 'FLOAT',
  }, {
    label: 'UNSIGNED_FLOAT',
    value: 'UNSIGNED_FLOAT',
  }, {
    label: 'DOUBLE',
    value: 'DOUBLE',
  }, {
    label: 'UNSIGNED_DOUBLE',
    value: 'UNSIGNED_DOUBLE',
  }, {
    label: 'DECIMAL',
    value: 'DECIMAL',
  }, {
    label: 'BOOLEAN',
    value: 'BOOLEAN',
  }, {
    label: 'TIME',
    value: 'TIME',
  }, {
    label: 'DATE',
    value: 'DATE',
  }, {
    label: 'TIMESTAMP',
    value: 'TIMESTAMP',
  }, {
    label: 'UNSIGNED_TIME',
    value: 'UNSIGNED_TIME',
  }, {
    label: 'UNSIGNED_DATE',
    value: 'UNSIGNED_DATE',
  }, {
    label: 'UNSIGNED_TIMESTAMP',
    value: 'UNSIGNED_TIMESTAMP',
  }, {
    label: 'CHAR',
    value: 'CHAR',
  }, {
    label: 'BINARY',
    value: 'BINARY',
  }, {
    label: 'VARBINARY',
    value: 'VARBINARY',
  }
  // , {
  //   label: 'ARRAY',
  //   value: 'ARRAY',
  // }
]

export const TYPE_WITH_SCALE = [
  'DECIMAL',
  'VARCHAR',
  'CHAR',
  'BINARY',
  // 'ARRAY'
]

export const TYPE_REQUIRED_SCALE = [
  'CHAR',
  'BINARY'
]

export const TYPE_WITH_PRECISION = [
  'DECIMAL'
]

export const TIME_DEFAULT_TYPE_LIST = [
  {
    label: '当前时间',
    value: '当前时间',
  }, {
    label: '自定义日期',
    value: '自定义日期',
  }, {
    label: '自定义表达式',
    value: '自定义表达式',
  },
]

/**
 * table column table 的 column 定义（好绕hhh
 */
export const COLUMNS = [
  {
    title: '列名',
    dataIndex: 'name',
    fixed: 'left',
    key: 'name',
    width: '100px',
    slots: { customRender: 'name' },
  },
  {
    title: '中文名',
    dataIndex: 'comment',
    key: 'comment',
    width: '100px',
    slots: { customRender: 'comment' },
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width: '160px',
    slots: { customRender: 'type' },
  },
  {
    title: '列族',
    dataIndex: 'columnFamily',
    key: 'columnFamily',
    width: '80px',
    slots: { customRender: 'columnFamily' },
  },
  {
    title: '长度',
    dataIndex: 'scale',
    key: 'scale',
    width: '60px',
    slots: { customRender: 'scale' },
  },
  {
    title: '精度',
    dataIndex: 'precision',
    key: 'precision',
    width: '60px',
    slots: { customRender: 'precision' },
  },
  {
    title: '主键',
    dataIndex: 'isPrimary',
    key: 'isPrimary',
    width: '30px',
    slots: { customRender: 'isPrimary' },
  },
  {
    title: '操作',
    key: 'action',
    width: '60px',
    slots: { customRender: 'action' },
  },
]

export const STORAGE_FORMAT_LIST = [
  // 暂未有支持计划
  // {
  //   label: 'row',
  //   value: 'row',
  // },
  {
    label: 'csv',
    value: 'csv',
  }, {
    label: 'text',
    value: 'text',
  }, {
    label: 'orc',
    value: 'orc',
  },
]

export const LANGUAGE_OPTION_LIST = [
  // {
  //   label: 'plpython',
  //   value: 'plpython',
  // },
  {
    label: 'plpgsql',
    value: 'plpgsql',
  },
  // {
  //   label: 'plperl',
  //   value: 'plperl',
  // },
]

export const ARG_MODE_LIST = [
  {
    label: 'IN',
    value: 'IN',
  }, {
    label: 'OUT',
    value: 'OUT',
  }, {
    label: 'INOUT',
    value: 'INOUT',
  },
]
