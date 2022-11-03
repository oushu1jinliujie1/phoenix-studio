import { Ref } from 'vue'
import xlsx from 'xlsx'
import moment from 'moment'
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

// export function parseSchema(file: any, config: any, complete: (fields: any) => void) {
//   const analyzeRow = (fieldsHash: any, row: any) => {
//     for (const key in row) {
//       const value = row[key]
//       const field = fieldsHash[key] || (fieldsHash[key] = { typesFound: {}, sample: null, maxLength: 0, enabled: true })

//       // Tally the presence of this field type
//       const type = detectType(value)
//       if (!field.typesFound[type]) field.typesFound[type] = 0
//       field.typesFound[type]++

//       // Save a sample record if there isn't one already (earlier rows might have an empty value)
//       if (!field.sample && value) {
//         field.sample = value
//       }

//       // Save the largest length
//       field.maxLength = Math.max(field.maxLength, value.length)
//     }
//   }

//   const detectType = (sample: any) => {
//     if (sample === '') {
//       return 'null'
//     } else if (sample.includes('-') && moment(sample, 'YYYY-MM-DD', true).isValid()) {
//       return 'date'
//     } else if (sample.includes('-') && moment(sample, moment.ISO_8601, true).isValid()) {
//       return 'datetime'
//       // } else if (moment(sample, 'X', true).isValid() && +sample >= 31536000) {
//       //     return 'timestamp'
//     } else if (!isNaN(sample) && sample.includes('.')) {
//       return 'float'
//     } else if (['true', 'false'].includes(sample.toLowerCase())) { // sample === '1' || sample === '0' ||
//       return 'boolean'
//     } else if (!isNaN(sample)) {
//       return Math.abs(sample) < 2147483647 ? 'integer' : 'long'
//     } else if (sample.length > 255) {
//       return 'text'
//     } else {
//       return 'text'
//     }
//   }

//   const analyzeRowResults = (fieldsHash: any) => {
//     const fieldsArray = []
//     let keys = null
//     if (fieldsHash.meta) {
//       keys = fieldsHash.meta.fields
//     } else {
//       keys = Object.keys(fieldsHash)
//     }
//     for (const key of keys) {
//       const field = fieldsHash[key]
//       // Determine which field type wins
//       field.type = determineWinner(field.typesFound)
//       // field.machineName = slug(key, {
//       //     replacement: '_',
//       //     lower: true
//       // })
//       field.machineName = key
//       field.sourceName = key
//       // If any null values encountered, set field nullable
//       if (field.typesFound.null) {
//         field.nullable = true
//       }
//       fieldsArray.push(field)
//     }
//     return fieldsArray
//   }

//   /**
//    *  Determine which type wins
//    *  - timestamp could be int
//    *  - integer could be float
//    *  - everything could be string
//    *  - if detect an int, don't check for timestamp anymore, only check for float or string
//    *  - maybe this optimization can come later...
//    */
//   const determineWinner = (fieldTypes: any) => {
//     const keys = Object.keys(fieldTypes)

//     if (keys.length === 1) {
//       return keys[0]
//     } else if (fieldTypes.text) {
//       return 'text'
//     } else if (fieldTypes.string) {
//       return 'string'
//     } else if (fieldTypes.float) {
//       return 'float'
//     } else if (fieldTypes.long) {
//       return 'long'
//     } else if (fieldTypes.integer) {
//       return 'integer'
//     } else { // TODO: if keys.length > 1 then... what? always string? what about date + datetime?
//       return fieldTypes[0]
//     }
//   }

//   const fieldsHash: any = {}
//   let rowCount = 0
//   let _config = {
//     header: true,
//     skipEmptyLines: true,
//     worker: true,
//     preview: 1000,
//     step: (row: any) => {
//       rowCount++
//       if (row.meta.fields && !('meta' in fieldsHash)) {
//         fieldsHash.meta = row.meta
//       }
//       analyzeRow(fieldsHash, row.data)
//     },
//     complete: () => {
//       const fieldsArray = analyzeRowResults(fieldsHash)
//       complete(fieldsArray)
//     }
//   }
//   _config = { ..._config, ...config }
//   Papa.parse(file, _config)
// }

interface CloumnData {
  columnName: string,
  comment: string,
  familyName: string,
  dataType: string,
  scale: number,
  precision: number,
  pk: boolean,
}
interface BasicTableData {
  schemaName?: string,
  tableName: string,
  comment: string,
  splitOn: string,
  saltBuckets: number,
  columns: CloumnData[],
}

export function parseBasicTableFromExcel(schemaName: string, fileBuffer: any) {
  if (!fileBuffer) return []
  const fileData = xlsx.read(fileBuffer)
  const basicTableList: BasicTableData[] = []
  const basicHeaders = ['表名', '表备注', 'split_on', 'SALT_BUCKETS']
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
      header: ['columnName', 'comment', 'familyName', 'dataType', 'scale', 'precision', 'pk']
    })
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
