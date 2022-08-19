/**
 * 工作簿收藏表
 */
export interface CollectTable {
  id: number,
  created_at: string,
  updated_at: string,
  worksheet_id: number,
  instance_id: number,
  instance: string,
  database: string,
  schema: string,
  table: string,
  // 表的id
  oid: string,
  // table 额外信息的 JSON 字符串
  table_info: string,
  creator_id: number,
  columns?: ColumnResData[],
  // 自定义属性：记录是否 hover
  ellipsisHover?: boolean,
}

/**
 * 获取工作簿信息
 */
export interface WorksheetInformation {
  sheet: any,
  vc_name: string,
  collections: CollectTable[],
  sql: {
    id: number,
    created_at: string,
    updated_at: string,
    worksheet_id: number,
    instance: string,
    database: string,
    schema: string,
    sql_text: string,
    remarks: string,
    save_type: number,
    creator_id: number,
  },
  history: {
    id: number,
    instance_id: number,
    instance: string,
    sql_text: string,
    status: string,
    query_id: string,
    rows: number,
    duration: number,
    start: string,
    end: string,
    message: string,
    updated_at: string,
    creator_id: number,
    creator_name: string,
    creator_remark: string,
  },
  history_result: {
    table_result: string,
    chart_result: string,
  }
}

/**
 * 数据库
 */
export interface Database {
  name: string,
  new_name: string,
  owner: string,
  comment: string,
  encoding: string,
  template: string,
  table_space: string,
  collation: string,
  character_type: string,
  limit: string,
  schema_count: number,
  is_template: string,
  privileges: null,
}

/**
 * 模式
 */
export interface Schema {
  name: string,
  owner: string,
  comment: string,
  new_name: string,
  drop_cascade: boolean,
  new_owner: string,
  new_comment: string,
  privileges: null,
  table_space?: any
}

/**
 * 表
 */
export interface Table {
  oid: string,
  name: string,
  split_on: string,
  salt_buckets: string,
  owner: string,
  schema: string,
  table_space: string,
  partitioned_table: boolean,
  comment: string,
  columns: null | ColumnResData[],
  drop_cascade: boolean,
  partition_keys: [] | null,
  partition_type: any | '',
  partition_default: string,
  sub_partitions: {
    name: string,
    partition_table: boolean,
    partition_type: string,
    partition_keys: null,
    range: string,
    in_value: string,
    modulus: number,
    remainder: number,
    sub_partitions: [],
    schema: string,
  }[] | null,
  privileges: null,
  table_type: string,
  hawq_sub_partitioned: boolean,
  hawq_sub_partition_type: any | '',
  hawq_sub_partition_keys: [] | null,
  hawq_sub_partitions: {
    name: string,
    partition_table: boolean,
    partition_type: string,
    partition_keys: null,
    range: string,
    in_value: string,
    modulus: number,
    remainder: number,
    sub_partitions: [],
    schema: string,
  }[] | null,
  hawq_partition_default: string,
  hawq_pk: string,
  hawq_dk: {
    att_num?: string,
    column_name: string
  }[] | null,
  location: string,
  external: boolean,
  // 自定义属性：记录是否收藏
  collected?: boolean,
  // 自定义属性: 记录收藏的 id
  collectedId?: number,
  // 自定义属性：记录是否 hover
  ellipsisHover?: boolean,
  // 自定义属性，记录 table 当前 database
  databaseName?: string,
}

/**
 * 获取表的接口
 */
export interface TableData {
  data: Table[],
  totalCount: number,
}

export interface ColumnResData {
  name: string,
  comment: string,
  column_family: string,
  type: string,
  length: number,
  scale: number,
  primary: boolean,
  // 列所属表单的 oid
  att_rel_id?: string,
  // 列在表单中的排序
  att_num?: string,
}

/**
 * 视图
 */
export interface View {
  name: string,
  owner: string,
  schema: string,
  comment: string,
  check_option: string,
  security_barrier: boolean,
  code: string,
  drop_cascade: boolean,
  new_name: string,
  new_schema: string,
  // 自定义属性：记录是否 hover
  ellipsisHover?: boolean,
}

/**
 * 获取视图的接口
 */
export interface ViewData {
  data: View[],
  totalCount: number,
}

/**
 * 函数
 */
export interface DBFunction {
  oid: string,
  proname: string,
  type: string,
  // 自定义属性：记录是否 hover
  ellipsisHover?: boolean,
}

/**
 * 执行结果
 */
export interface ExecuteResult {
  worksheet_history: any,
  result: {
    header: [
      string,
      string,
      string,
      string
    ],
    types: [
      string,
      string,
      string,
      string
    ],
    origin_types: [
      string,
      string,
      string,
      string
    ],
    body: [
      [
        string,
        string,
        string,
        string
      ],
      [
        string,
        string,
        string,
        string
      ],
      [
        string,
        string,
        string,
        string
      ],
      [
        string,
        string,
        string,
        string
      ]
    ]
  }
}

/**
 * 工作簿对象
 */
export interface Worksheet {
  id: number,
  name: string,
  folder_id: number,
  folder_name: string,
  updated_at: string,
  creator_id: number,
  creator_name: string,
  creator_remark: string,
}

/**
 * 获取最近访问工作簿接口返回格式
 */
export interface WorkSheetRes {
  data: Worksheet[],
  total: number,
}

/**
 * 函数详情
 */
export interface FunctionDetail {
  comment: string
  lanname: string
  nspname: string
  proname: string
  prosrc: string
  typname: string
  usename: string
}

/**
 * 函数参数
 */
export class FunctionArg {
  name = ''
  type = ''
  mode = ''

  constructor(
    name = '',
    type = '',
    mode = '',
  ) {
    this.name = name
    this.type = type
    this.mode = mode
  }
}

export interface WorksheetFolder {
  id: number
  name: string
  updated_at: string
  creator_id: number
  creator_name: string
  creator_remark: string
}

export interface WorksheetFoldParams {
  pageNum?: number,
  pageSize?: number,
  sortField?: string,
  // 排序顺序 1asc -1desc
  sortOrder?: number,
  // 模糊搜索 文件夹名/用户名/用户备注
  search?: string,
  updateTimeEnd?: string,
  updateTimeBegin?: string,
}

export interface WorksheetParams extends WorksheetFoldParams{
  folderId?: number
}

export interface WorksheetAndWorksheetFolderParams {
  pageNum?: number,
  pageSize?: number,
  sortField?: string,
  // 排序顺序 1asc -1desc
  sortOrder?: number,
  // 模糊搜索 文件夹名/用户名/用户备注
  search?: string,
  updateTimeEnd?: string,
  updateTimeBegin?: string,
}

export interface WorksheetFolderData {
  data: WorksheetFolder[]
  total: number
}