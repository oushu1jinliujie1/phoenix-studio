<template>
  <div class="connection-table-settings">
    <!-- 已选表标签列表 -->
    <div class="connection-table-settings-tags">
      <div class="connection-table-settings-tags-icon">
        <icon image name="worksheet/search_table_two_color"/>
      </div>
      <span class="connection-table-settings-tags-item" v-for="(table, index) in selectTableList" :key="table.schema + table.name">
        <icon v-if="index !== 0" color="#336CFF" name="worksheet/arrow_double" class="connection-table-settings-tags-item-prefix"/>
        <x-tag closable class="selected-table-tag" @close="removeTableTag(table)">
          {{ table.schema + ': ' + table.name }}
        </x-tag>
      </span>
      <div v-show="selectTableList.length === 0" class="selected-table-tag-placeholder">
        请选择需要关联查询的表（表之间需第一主键列匹配）
      </div>
    </div>
    <!-- 选表主体 -->
    <div class="connection-table-settings-main">
      <!-- 模式选择 -->
      <div class="connection-table-settings-schema-container">
        <div class="connection-table-settings-container-title">
          <span>模式</span>
          <icon color="#336CFF" name="worksheet/refresh" style="cursor: pointer;" @click="refreshSchemaList()"/>
        </div>
        <x-spin :spinning="schema.spinning">
          <!-- 搜索 -->
          <div class="connection-table-settings-container-search">
            <x-input-search
              v-model:value="schema.searchValue"
              allow-clear
              class="raw"
              data-test-id="connection-table-settings-schema-search-input"
              placeholder="请输入模式名，按回车搜索"
              @search="handleSchemaOnSearch"
              @keyup.enter="handleSchemaOnSearch"
            />
          </div>
          <!-- schema列表 -->
          <div class="connection-table-settings-list">
            <div
              v-for="(_schema, index) in (schema.list as any[])" :key="index"
              :class="[_schema.name === currentSchema.name ? 'current-select' : '']"
              class="connection-table-settings-list-item"
              @click="selectCurrentSchema(_schema)"
            >
              <div class="item-content" :title="_schema.name">
                
                <icon v-show="_schema.name !== currentSchema.name" name="worksheet/schema"/>
                <icon v-show="_schema.name === currentSchema.name" image name="worksheet/schema_selected"/>
                {{ _schema.name }}
              </div>
              <div class="item-content">
                <icon name="next"/>
              </div>
            </div>
          </div>
        </x-spin>
      </div>
      <!-- 表选择 -->
      <div class="connection-table-settings-table-container">
        <div class="connection-table-settings-container-title">
          <span>表</span>
          <icon color="#336CFF" name="worksheet/refresh" style="cursor: pointer;" @click="refreshTableList()"/>
        </div>
        <x-spin :spinning="table.spinning">
          <!-- 搜索 -->
          <div class="connection-table-settings-container-search">
            <x-input-search
              v-model:value="table.searchValue"
              allow-clear
              class="raw"
              data-test-id="connection-table-settings-table-search-input"
              placeholder="请输入表名，按回车搜索"
              @search="handleTableOnSearch"
              @keyup.enter="handleTableOnSearch"
            />
          </div>
          <!-- table列表 -->
          <div class="connection-table-settings-list">
            <x-tooltip
              v-for="(_table, index) in (table.list as any[])"
              :key="index"
              :overlayStyle="{ 'pointer-events': 'none' }"
              :visible="isDisabled(_table)?undefined:false">
              <template #title>
                <div style="color: #D74472;white-space: nowrap;">该表的第1个主键列与其他所选表不匹配</div>
              </template>
              <div
                :class="[_table.selected ? 'current-select' : '', isDisabled(_table) ? 'table-disabled' : '']"
                class="connection-table-settings-list-item"
              >
                <div class="item-content-half" :title="_table.name">
                  <x-checkbox
                    :disabled="isDisabled(_table)"
                    v-model:checked="_table.selected"
                    @change="onChangeTableSelected(_table)"
                  />
                  <icon v-show="!_table.selected" name="worksheet/table_line"/>
                  <icon v-show="_table.selected" name="worksheet/table_selected"/>
                  {{ _table.name }}
                </div>
                <div class="item-content-half" :title="_table.primary_columns[0].name">
                  <icon name="worksheet/key"/>
                  <span>{{ _table.primary_columns[0].name }}</span>&nbsp;
                  <span>{{ '(' + _table.primary_columns[0].type + ')' }}</span>
                </div>
              </div>
            </x-tooltip>
          </div>
        </x-spin>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
/* eslint-disable vue/no-side-effects-in-computed-properties */
import { defineComponent, ref, computed, reactive, toRefs, onBeforeMount, PropType, watch } from 'vue'
import Icon from '@/components/Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { getSchemaList, getTableList } from '@/api'
import { debounce, throttle } from 'lodash'
import { message } from 'ant-design-vue-3'

export default defineComponent({
  name: 'connectionTableSettings',
  props: {
    selectTableList: {
      type: Array as PropType<any[]>,
      required: true
    }
  },
  components: { Icon, ...smartUI },
  emits: ['update:selectTableList'],
  setup(props, context) {
    const state = reactive({
      schema: {
        spinning: false,
        searchValue: '',
        list: [] as any[],
      },
      table: {
        spinning: false,
        searchValue: '',
        list: [] as any[],
      },
      currentSchema: null as any
    })
    const notFinishMsgRef = ref('')
    const isFinishRef = computed(() => {
      if (props.selectTableList.length === 0) {
        notFinishMsgRef.value = '请选择需要关联的表'
        return false
      } else {
        notFinishMsgRef.value = ''
        return true
      }
    })

    const handleGetSchemaList = async() => {
      state.schema.spinning = true
      const result = await getSchemaList()
      if (result.meta.success) {
        const META_SCHEMA = [
          'SYSTEM',
          'CT',
        ]
        state.schema.list = (result.data ?? [])
          .filter((schema: any) => !META_SCHEMA.includes(schema.TABLE_SCHEM))
          .map((schema: any) => {
            return {
              name: schema.TABLE_SCHEM
            }
          })
      } else {
        state.schema.list = []
        message.error(`获取模式失败：${result.meta.message}`)
      }
      const current = state.schema.list.find((item: any) => item.name === state.currentSchema?.name)
      state.currentSchema = current || state.schema.list[0] || null
      state.schema.spinning = false
    }

    const handleGetTableList = async() => {
      if (!state.currentSchema) return
      state.table.spinning = true
      const result = await getTableList({
        schemaName: state.currentSchema.name,
        tableName: state.table.searchValue,
        limit: -1,
        offset: 0
      })

      if (result.meta.success) {
        state.table.list = (result.data.data ?? []).map((table: any) => {
          return {
            schema: state.currentSchema.name,
            name: table.TABLE_NAME,
            primary_columns: table.PK_COLUMNS.sort((a: any, b: any) => a.ORDINAL_POSITION - b.ORDINAL_POSITION).map((column: any) => {
              return {
                name: column.COLUMN_NAME,
                type: column.DATA_TYPE_NAME,
                schema: column.TABLE_SCHEM,
                table: column.TABLE_NAME,
                column_family: column.COLUMN_FAMILY,
                order: column.ORDINAL_POSITION,
                primary: true
              }
            }),
            selected: props.selectTableList.findIndex((item: any) => item.name === table.TABLE_NAME && item.schema === state.currentSchema.name) !== -1,
          }
        })
      } else {
        message.error(`刷新表失败：${result.meta.message}`)
      }

      state.table.spinning = false
    }

    const removeTableTag = (table: any) => {
      const _table = state.table.list.find((item: any) => item.name === table.name && item.schema === table.schema)
      if (_table) _table.selected = false
      const selectTableList = props.selectTableList.filter((item: any) => item.name !== table.name || item.schema !== table.schema)
      context.emit('update:selectTableList', selectTableList)
    }

    const onChangeTableSelected = (table: any) => {
      if (table.selected) {
        const selectTableList = [...props.selectTableList, table]
        context.emit('update:selectTableList', selectTableList)
      } else {
        removeTableTag(table)
      }
    }

    const refreshSchemaList = throttle(async() => {
      await handleGetSchemaList()
    }, 500)

    const refreshTableList = throttle(async() => {
      await handleGetTableList()
    }, 500)

    const handleSchemaOnSearch = debounce(async() => {
      await handleGetSchemaList()
    }, 500)

    const handleTableOnSearch = debounce(async() => {
      await handleGetTableList()
    }, 500)

    const selectCurrentSchema = debounce(async(schema: any) => {
      if (schema.name === state.currentSchema.name) return
      state.currentSchema = schema
      await handleGetTableList()
    }, 500)

    const isDisabled = (table: any) => {
      if (!props.selectTableList.length) return false
      const primary = props.selectTableList[0].primary_columns[0]
      const pk = table.primary_columns[0]
      if (pk.type === primary.type) return false
      return true
    }

    onBeforeMount(async() => {
      await handleGetSchemaList()
      await handleGetTableList()
    })

    return {
      ...toRefs(state),

      notFinishMsgRef,
      isFinishRef,

      handleGetSchemaList,
      handleGetTableList,
      refreshSchemaList,
      refreshTableList,
      handleSchemaOnSearch,
      handleTableOnSearch,
      selectCurrentSchema,

      removeTableTag,
      onChangeTableSelected,
      isDisabled,
    }
  }
})

</script>

<style lang="scss">
.connection-table-settings {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 4px;
  border: 1px solid #D5D8D8;

  .connection-table-settings-tags {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    row-gap: 6px;
    height: 86px;
    flex-shrink: 0;
    padding: 15px 58px;
    border-bottom: 1px solid #D5D8D8;

    .connection-table-settings-tags-icon {
      position: absolute;
      top: 18px;
      left: 30px;
      height: 18px;
      width: 18px;
    }

    .selected-table-tag,
    .selected-table-tag:active {
      margin-right: 0;
      height: 24px;
      padding: 2px 8px;
      background: rgba(213, 216, 219, 0.5);
      border-radius: 4px;
      color: #282B2E;
      font-size: 14px;
      line-height: 20px;
    }

    .selected-table-tag-placeholder {
      height: 24px;
      padding: 3px 0 1px;
      line-height: 20px;
      color: #85888C;
      user-select: none;
    }
  }

  .connection-table-settings-main {
    flex-grow: 1;
    height: 0;
    display: flex;

    .connection-table-settings-schema-container {
      padding: 10px 0;
      width: 40%;
      border-right: 1px solid #D5D8D8;
    }
    .connection-table-settings-table-container {
      padding: 10px 0;
      width: 60%;
    }

    .connection-table-settings-container-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 26px;
      padding-bottom: 8px;
      margin: 0 30px;
      border-bottom: 1px solid #D5D8D8;
      line-height: 18px;
      font-size: 12px;

      > span {
        color: #85888C;
      }
    }
    .connection-table-settings-container-search {
      margin: 0 30px;
      padding: 5px 0;
    }

    .connection-table-settings-list {

      .connection-table-settings-list-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        white-space: nowrap;
        @include font-normal();
        border: 1px solid transparent;
        border-radius: 4px;
        margin: 0 10px;
        padding: 0 20px;
        height: 32px;
        cursor: pointer;

        > .item-content-half {
          display: flex;
          align-items: center;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          width: 50%;
          
          .smartui-checkbox {
            flex-shrink: 0;
            margin-right: 5px;
          }

          .icon {
            flex-shrink: 0;
            margin-right: 5px;
          }
        }

        > .item-content:nth-of-type(1) {
          display: flex;
          align-items: center;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;

          .icon {
            flex-shrink: 0;
            margin-right: 5px;
          }
        }

        > .item-content:nth-of-type(2) {
          opacity: 0;
        }

        &:hover,
        &:focus {
          background: rgba(51, 108, 255, 0.2);
        }
        &:hover,
        &:focus,
        &.current-select {
          border-radius: 4px;
          color: #336CFF;

          > .item-content:nth-of-type(2) {
            opacity: 1;
          }
        }
        &.table-disabled {
          background: none;
          color: #282B2E;

          &:hover {
            border: 1px solid #D74472;
          }
        }
      }
    }
  }
}

</style>
