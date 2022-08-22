/**
 * 列类型
 */
export const TYPE_OPTION_LIST = [
  {
    label: 'byte',
    value: 'byte',
  }, {
    label: 'int2',
    value: 'int2',
  }, {
    label: 'int4',
    value: 'int4',
  }, {
    label: 'int8',
    value: 'int8',
  }, {
    label: 'money',
    value: 'money',
  }, {
    label: 'json',
    value: 'json',
  }, {
    label: 'float4',
    value: 'float4',
  }, {
    label: 'float8',
    value: 'float8',
  }, {
    label: 'name',
    value: 'name',
  }, {
    label: 'bool',
    value: 'bool',
  }, {
    label: 'date',
    value: 'date',
  }, {
    label: 'point',
    value: 'point',
  }, {
    label: 'timetz',
    value: 'timetz',
  }, {
    label: 'timestamptz',
    value: 'timestamptz',
  }, {
    label: 'numeric',
    value: 'numeric',
  }, {
    label: 'text',
    value: 'text',
  }, {
    label: 'varchar',
    value: 'varchar',
  }, {
    label: 'char',
    value: 'char',
  }, {
    label: 'bit',
    value: 'bit',
  }, {
    label: 'time',
    value: 'time',
  }, {
    label: 'timestamp',
    value: 'timestamp',
  },
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
 * 分区类型
 */
export const PARTITION_TYPE_OPTION_LIST = [
  {
    label: 'range',
    value: 'range',
  },
  {
    label: 'list',
    value: 'list',
  },
]

/**
 * 分区类型为 range 时，是否时等值分区的选择
 */
export const RANGE_PARTITION_TYPE_OPTION_LIST = [
  {
    label: '自定义分区',
    value: 0,
  },
  {
    label: '等值分区',
    value: 1,
  },
]

/**
 * table column table 的 column 定义（好绕hhh
 */
export const COLUMNS = [
  {
    title: '列名',
    dataIndex: 'name',
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
    width: '80px',
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
    dataIndex: 'length',
    key: 'length',
    width: '60px',
    slots: { customRender: 'length' },
  },
  {
    title: '精度',
    dataIndex: 'scale',
    key: 'scale',
    width: '60px',
    slots: { customRender: 'scale' },
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

/**
 * 分区区间类型为 range，且非等值区间时的 table 的 columns 定义
 */
export const COLUMNS_FOR_RANGE_NON_EVEN_PARTITION = [
  {
    title: '分区名',
    dataIndex: 'name',
    key: 'name',
    width: '40%',
    slots: { customRender: 'name' },
  },
  {
    title: '分区开始值',
    dataIndex: 'partitionBegin',
    key: 'partitionBegin',
    width: '40%',
    slots: { customRender: 'partitionBegin' },
  },
  {
    title: '包含开始值',
    dataIndex: 'isIncludeBegin',
    key: 'isIncludeBegin',
    width: '20%',
    slots: { customRender: 'isIncludeBegin' },
  },
]

/**
 * 分区区间类型为 range，且为等值区间时的 table 的 columns 定义
 */
export const COLUMNS_FOR_RANGE_EVEN_PARTITION = [
  {
    title: '分区名',
    dataIndex: 'name',
    key: 'name',
    width: '15%',
    slots: { customRender: 'name' },
  },
  {
    title: '分区开始值',
    dataIndex: 'partitionBegin',
    key: 'partitionBegin',
    width: '20%',
    slots: { customRender: 'partitionBegin' },
  },
  {
    title: '包含开始值',
    dataIndex: 'isIncludeBegin',
    key: 'isIncludeBegin',
    width: '12.5%',
    slots: { customRender: 'isIncludeBegin' },
  },
  {
    title: '分区结束值',
    dataIndex: 'partitionEnd',
    key: 'partitionEnd',
    width: '20%',
    slots: { customRender: 'partitionEnd' },
  },
  {
    title: '包含结束值',
    dataIndex: 'isIncludeEnd',
    key: 'isIncludeEnd',
    width: '12.5%',
    slots: { customRender: 'isIncludeEnd' },
  },
  {
    title: 'interval',
    dataIndex: 'interval',
    key: 'interval',
    width: '20%',
    slots: { customRender: 'interval' },
  },
]

/**
 * 分区区间类型为 list 的 table 的 columns 定义
 */
export const COLUMNS_FOR_LIST_PARTITION = [
  {
    title: '分区名',
    dataIndex: 'name',
    key: 'name',
    width: '30%',
    slots: { customRender: 'name' },
  },
  {
    title: '分区区间',
    dataIndex: 'partitionSection',
    key: 'partitionSection',
    width: '70%',
    slots: { customRender: 'partitionSection' },
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
