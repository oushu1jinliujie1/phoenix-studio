export const mockData = (...args: any): Promise<any> => {
  const data = {
    data: {},
    meta: {
      success: true,
      status_code: '',
      params: null,
      message: ''
    }
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(data)
    }, 500)
  })
}

// /////////////// 主页面
// schema
export const getSchemaList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': [
      {
        'name': 'hawq_toolkit',
        'owner': 'oushu',
        'comment': '',
        'new_name': '',
        'drop_cascade': false,
        'new_owner': '',
        'new_comment': '',
        'privileges': null
      },
      {
        'name': 'information_schema',
        'owner': 'oushu',
        'comment': '',
        'new_name': '',
        'drop_cascade': false,
        'new_owner': '',
        'new_comment': '',
        'privileges': null
      },
      {
        'name': 'pg_aoseg',
        'owner': 'oushu',
        'comment': 'Reserved schema for Append Only segment list and eof tables',
        'new_name': '',
        'drop_cascade': false,
        'new_owner': '',
        'new_comment': '',
        'privileges': null
      },
      {
        'name': 'pg_bitmapindex',
        'owner': 'oushu',
        'comment': 'Reserved schema for internal relations of bitmap indexes',
        'new_name': '',
        'drop_cascade': false,
        'new_owner': '',
        'new_comment': '',
        'privileges': null
      },
      {
        'name': 'pg_catalog',
        'owner': 'oushu',
        'comment': 'System catalog schema',
        'new_name': '',
        'drop_cascade': false,
        'new_owner': '',
        'new_comment': '',
        'privileges': null
      },
      {
        'name': 'public',
        'owner': 'oushu',
        'comment': 'Standard public schema',
        'new_name': '',
        'drop_cascade': false,
        'new_owner': '',
        'new_comment': '',
        'privileges': null
      }
    ]
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlForCreateSchema = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': 'SQL FOR CREATE SCHEMA'
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const deleteSchema = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const duplicateSchema = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// SQL相关
export const executeSql = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlStatus = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlResult = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// /////////////// 基础表
// 基础表操作
export const getTableList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': {
      'data': [
        {
          'oid': '17217',
          'name': 'tag_run_census_735ad432e97f4788b98c8728002860fd',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17172',
          'name': 'tag_run_census_787c55fb20b4484a980ea4828c9c16b2',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17024',
          'name': 'tag_run_census_a08f53a4a68743cbadf22babb5582c5b',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '16739',
          'name': 'tag_run_census_b937ab45b3044fe0b06953eb510fd300',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17019',
          'name': 'tag_run_census_b9bf0a0af6d7407893c654a2f77c6fc6',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17222',
          'name': 'tag_run_census_d338dcc91b454776a07f30e51372f2ff',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17009',
          'name': 'tag_run_census_d8ee05060c6b4b95b3fdccc1a6e81ff7',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '16996',
          'name': 'tag_run_res_219b16475ec0413288b86f626329f146',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17001',
          'name': 'tag_run_res_40f7ab9e06944651b6015aaeac889ddf',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17011',
          'name': 'tag_run_res_4476af4a25194edb9977081b4899c77b',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17477',
          'name': 'tag_run_res_508fde4c7016443d9781b32d2009355a',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '16849',
          'name': 'tag_run_res_5356618bd2734604ba9f8309295c7a6f',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '16823',
          'name': 'tag_run_res_5d7c094434354fcb97573d87cf29fb33',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17036',
          'name': 'tag_run_res_6680116d9d5144418acade8db7fe0d12',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17164',
          'name': 'tag_run_res_6e40a8a961224750bfdd8f5213be6821',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17214',
          'name': 'tag_run_res_735ad432e97f4788b98c8728002860fd',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17169',
          'name': 'tag_run_res_787c55fb20b4484a980ea4828c9c16b2',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17021',
          'name': 'tag_run_res_a08f53a4a68743cbadf22babb5582c5b',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '16736',
          'name': 'tag_run_res_b937ab45b3044fe0b06953eb510fd300',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        },
        {
          'oid': '17016',
          'name': 'tag_run_res_b9bf0a0af6d7407893c654a2f77c6fc6',
          'split_on': '',
          'salt_buckets': 0,
          'schema': 'public',
          'table_space': '',
          'partitioned_table': false,
          'comment': '',
          'columns': null,
          'drop_cascade': false,
          'partition_default': '',
          'partition_keys': null,
          'partition_type': '',
          'sub_partitions': null,
          'privileges': null,
          'table_type': 'orc',
          'hawq_partition_default': '',
          'hawq_sub_partitioned': false,
          'hawq_sub_partition_type': '',
          'hawq_sub_partition_keys': null,
          'hawq_sub_partitions': null,
          'hawq_pk': '',
          'hawq_dk': null,
          'location': '',
          'external': false
        }
      ],
      'totalCount': 49
    }
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlForCreateTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': 'SQL FOR CREATE TABLE'
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlForUpdateTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': 'SQL FOR UPDATE TABLE'
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const deleteTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const duplicateTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getTableDetails = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': {}
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// 列操作
export const getColumnList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': [
      {
        'att_rel_id': '17047',
        'att_num': '1',
        'schema': '',
        'table': '',
        'name': 'c1',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '2',
        'schema': '',
        'table': '',
        'name': 'c2',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '3',
        'schema': '',
        'table': '',
        'name': 'c3',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '4',
        'schema': '',
        'table': '',
        'name': 'c4',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '5',
        'schema': '',
        'table': '',
        'name': 'c5',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '6',
        'schema': '',
        'table': '',
        'name': 'c6',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '7',
        'schema': '',
        'table': '',
        'name': 'c7',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '8',
        'schema': '',
        'table': '',
        'name': 'c8',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '9',
        'schema': '',
        'table': '',
        'name': 'c9',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '10',
        'schema': '',
        'table': '',
        'name': 'c10',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '11',
        'schema': '',
        'table': '',
        'name': 'c11',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '12',
        'schema': '',
        'table': '',
        'name': 'c12',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '13',
        'schema': '',
        'table': '',
        'name': 'c13',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '14',
        'schema': '',
        'table': '',
        'name': 'c14',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '15',
        'schema': '',
        'table': '',
        'name': 'c15',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      },
      {
        'att_rel_id': '17047',
        'att_num': '16',
        'schema': '',
        'table': '',
        'name': 'c16',
        'comment': '',
        'type': 'VARCHAR',
        'length': 128,
        'scale': 0,
        'defaultvalue': '',
        'notnull': false,
        'autoinc': false,
        'partitionkey': false,
        'primary_key': false,
        'table_external': false
      }
    ]
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlForCreateColumn = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': 'SQL FOR CREATE COLUMN'
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getSqlForUpdateColumn = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': 'SQL FOR UPDATE COLUMN'
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const deleteColumn = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const duplicateColumn = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// 二级索引
export const getSecondaryIndexList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': {
      'data': [],
      'totalCount': 0
    }
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const createSecondaryIndex = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const deleteSecondaryIndex = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const duplicateSecondaryIndex = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// 表关联
export const getConnectionList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const cancelConnection = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// /////////////// 查询表
// 查询表操作
export const getSearchTableList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': {
      'data': [
        {
          'oid': '17217',
          'name': 'tag_run_census_735ad432e97f4788b98c8728002860fd',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17172',
          'name': 'tag_run_census_787c55fb20b4484a980ea4828c9c16b2',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17024',
          'name': 'tag_run_census_a08f53a4a68743cbadf22babb5582c5b',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '16739',
          'name': 'tag_run_census_b937ab45b3044fe0b06953eb510fd300',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17019',
          'name': 'tag_run_census_b9bf0a0af6d7407893c654a2f77c6fc6',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17222',
          'name': 'tag_run_census_d338dcc91b454776a07f30e51372f2ff',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17009',
          'name': 'tag_run_census_d8ee05060c6b4b95b3fdccc1a6e81ff7',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '16996',
          'name': 'tag_run_res_219b16475ec0413288b86f626329f146',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17001',
          'name': 'tag_run_res_40f7ab9e06944651b6015aaeac889ddf',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17011',
          'name': 'tag_run_res_4476af4a25194edb9977081b4899c77b',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17477',
          'name': 'tag_run_res_508fde4c7016443d9781b32d2009355a',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '16849',
          'name': 'tag_run_res_5356618bd2734604ba9f8309295c7a6f',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '16823',
          'name': 'tag_run_res_5d7c094434354fcb97573d87cf29fb33',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17036',
          'name': 'tag_run_res_6680116d9d5144418acade8db7fe0d12',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17164',
          'name': 'tag_run_res_6e40a8a961224750bfdd8f5213be6821',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17214',
          'name': 'tag_run_res_735ad432e97f4788b98c8728002860fd',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17169',
          'name': 'tag_run_res_787c55fb20b4484a980ea4828c9c16b2',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17021',
          'name': 'tag_run_res_a08f53a4a68743cbadf22babb5582c5b',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '16736',
          'name': 'tag_run_res_b937ab45b3044fe0b06953eb510fd300',
          'schema': 'public',
          'comment': '',
          'connection': ''
        },
        {
          'oid': '17016',
          'name': 'tag_run_res_b9bf0a0af6d7407893c654a2f77c6fc6',
          'schema': 'public',
          'comment': '',
          'connection': ''
        }
      ],
      'totalCount': 49
    }
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const createSearchTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const updateSearchTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const deleteSearchTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const duplicateSearchTable = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// 查询表新建/编辑流程 关联设置相关
export const getDatabaseList = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const getTableListByDatabase = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}
export const setConnection = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

// 数据查询
export const filterData = (...args: any): Promise<any> => {
  const resp = {
    'meta': {
      'success': true,
      'message': '',
      'status_code': '',
      'params': null
    },
    'data': null
  }
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve(resp)
    }, 500)
  })
}

