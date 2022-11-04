import xlsx from 'xlsx'
import { isEqual } from 'lodash'

export class FinishCheckItem {
  ref: { value: any } = { value: null }
  msg = ''

  constructor(ref: { value: any }, msg: string) {
    this.ref = ref
    this.msg = msg
  }
}

export function getIsFinish(refs: FinishCheckItem[]) {
  for (let i = 0; i < refs.length; i++) {
    const ref = refs[i].ref
    const msg = refs[i].msg
    const refValue = ref.value
    if ((!refValue) || (!refValue.isFinishRef)) {
      return { result: false, msg }
    }
  }
  return { result: true, msg: '' }
}
interface CloumnData {
  columnName: string,
  comment: string,
  familyName: string,
  dataType: string,
  scale: number | undefined,
  precision: number | undefined,
  pk: boolean,
}
interface BasicTableData {
  schemaName?: string,
  tableName: string,
  comment: string,
  splitOn: string | undefined,
  saltBuckets: number | undefined,
  columns: CloumnData[],
}

export function parseBasicTableFromExcel(schemaName: string, fileBuffer: any) {
  if (!fileBuffer) return []
  const fileData = xlsx.read(fileBuffer)
  const basicTableList: BasicTableData[] = []
  const basicHeaders = ['表名', '表备注', 'split_on', 'SALT_BUCKETS']
  console.log('fileData: ', fileData)
  for (const key in fileData.Sheets) {
    const sheet = fileData.Sheets[key]
    const basicTable: BasicTableData = {
      schemaName: schemaName,
      tableName: '',
      comment: '',
      splitOn: '',
      saltBuckets: 0,
      columns: [],
    }
    const tableHeaders = xlsx.utils.sheet_to_json(sheet, { range: 'A1:D1', header: 1 })[0]
    if (!isEqual(tableHeaders, basicHeaders)) continue
    const tableInfo = xlsx.utils.sheet_to_json(sheet, {
      range: 'A2:D2',
      header: ['tableName', 'comment', 'splitOn', 'saltBuckets']
    })[0]
    Object.assign(basicTable, tableInfo)
    if (!basicTable.tableName) continue
    const columnInfo = xlsx.utils.sheet_to_json(sheet, {
      range: 3,
      header: ['columnName', 'comment', 'familyName', 'dataType', 'scale', 'precision', 'pk'],
    })
    console.log('columnInfo: ', columnInfo)
    Object.assign(basicTable, { columns: columnInfo })
    basicTableList.push(basicTable)
  }
  return basicTableList
}

interface ConnectionColumn {
  schemaName: string,
  tableName: string,
  columnName: string
}
interface ConnectionTable {
  schemaName: string,
  tableName: string,
}
interface SearchTableData {
  queryName: string,
  chineseName: string,
  description: string,
  tableNames: ConnectionTable[],
  connections: ConnectionColumn[][],
}

interface ConnectionInfo {
  queryName: string,
  connectionColumns: Record<string, string>[]
}

export function parseSearchTableFromExcel(fileBuffer: any) {
  const searchTableList: any[] = []
  const connectionTableList: any[] = []
  if (!fileBuffer) return { searchTableList, connectionTableList }
  const fileData = xlsx.read(fileBuffer)
  const searchHeaders = ['查询表名', '查询表中文名', '查询表描述']
  for (const key in fileData.Sheets) {
    const sheet = fileData.Sheets[key]
    const searchTable: SearchTableData = {
      queryName: '',
      chineseName: '',
      description: '',
      tableNames: [],
      connections: [],
    }
    const connectionInfo: ConnectionInfo = {
      queryName: '',
      connectionColumns: [],
    }

    const tableHeaders = xlsx.utils.sheet_to_json(sheet, { range: 'A1:C1', header: 1 })[0]
    if (!isEqual(tableHeaders, searchHeaders)) continue

    const tableInfo = xlsx.utils.sheet_to_json(sheet, {
      range: 'A2:C2',
      header: ['queryName', 'chineseName', 'description']
    })[0]
    Object.assign(searchTable, tableInfo)
    Object.assign(connectionInfo, { queryName: searchTable.queryName })
    if (!searchTable.queryName) continue

    const connectionTables: any = xlsx.utils.sheet_to_json(sheet, { range: 2, header: 1 })[0]
    const tableNames = connectionTables?.map((tableStr: any) => ({ schemaName: tableStr.split('.')[0], tableName: tableStr.split('.')[1] }))
    Object.assign(searchTable, { tableNames })

    const connectionColumns: any[] = xlsx.utils.sheet_to_json(sheet, {
      range: 3,
      header: tableNames.map((table: any) => `schema.${table.schemaName}.table.${table.tableName}`)
    })

    Object.assign(connectionInfo, { connectionColumns })
    const connections: any[] = connectionColumns.map((connection: any) => {
      const res: any[] = []
      for (const table of tableNames) {
        res.push({
          schemaName: table.schemaName,
          tableName: table.tableName,
          columnName: connection[`schema.${table.schemaName}.table.${table.tableName}`]
        })
      }
      return res
    })
    Object.assign(searchTable, { connections })

    connectionTableList.push(connectionInfo)
    searchTableList.push(searchTable)
  }
  return { searchTableList, connectionTableList }
}

export function resolveExportExcel(title: string, fileBuffer: any) {
  const excelData = new Blob([fileBuffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  })
  const _a = document.createElement('a')
  _a.href = URL.createObjectURL(excelData)
  _a.target = '_blank'
  _a.download = title
  document.body.appendChild(_a)
  _a.click()
  document.body.removeChild(_a)
}
