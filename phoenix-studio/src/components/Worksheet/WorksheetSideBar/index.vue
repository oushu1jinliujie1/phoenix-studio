<template>
  <div class="v-oushudb-work-sheet-sidebar">
      <!-- schema 选择 -->
      <div class="work-sheet-sidebar-main">
        <div class="work-sheet-sidebar-selector">
          <div class="work-sheet-sidebar-selector-item">
            <div class="work-sheet-sidebar-selector-item-select">
              <x-select
                v-model:value="schemaSelectedName"
                show-search
                data-test-id="oushudb-worksheet-resource-schema-select"
                placeholder="请选择模式"
              >
                <x-select-option v-for="schema in schemaList" :key="schema.name">
                  {{ schema.name }}
                </x-select-option>
                <template #prefixIcon>
                  <icon image name="worksheet/schema"/>
                </template>
              </x-select>
              <a-dropdown>
                <x-button data-test-id="oushudb-worksheet-resource-database-extra-dropdown" icon-name="worksheet/ellipsis" type="text"/>
                <template #overlay>
                  <x-menu class="work-sheet-schema-ellipsis-overlay">
                    <x-menu-item
                      data-test-id="oushudb-worksheet-resource-schema-refresh-menu-item"
                      @click="handleRefreshSchema"
                    >
                      <icon color="inherit" name="worksheet/refresh"/>
                      刷新模式
                    </x-menu-item>
                    <x-menu-item
                      :disabled="schemaSelectedName === undefined "
                      data-test-id="oushudb-worksheet-resource-schema-copy-name-menu-item"
                      @click="handleCopySchemaName"
                    >
                      <icon color="inherit" name="worksheet/copy"/>
                      复制名称
                    </x-menu-item>
                    <x-menu-divider/>
                    <x-menu-item
                      data-test-id="oushudb-worksheet-resource-schema-add-menu-item"
                      @click="handleAddSchemaIconClick"
                    >
                      <icon color="inherit" image name="worksheet/schema_add"/>
                      新建模式
                    </x-menu-item>
                    <x-menu-item
                      :disabled="schemaSelectedName === undefined"
                      data-test-id="oushudb-worksheet-resource-schema-delete-menu-item"
                      @click="handleDeleteSchemaIconClick"
                    >
                      <icon color="inherit" name="worksheet/delete"/>
                      删除模式
                    </x-menu-item>
                    <x-menu-divider/>
                  </x-menu>
                </template>
              </a-dropdown>
            </div>
          </div>
        </div>
      </div>
      <!-- tabs -->
      <div class="work-sheet-sidebar-tabs">
        <x-spin :spinning="tabsLoading">
          <x-tabs v-model:active-key="activeTabKey">
            <!-- table 表 -->
            <x-tab-pane key="table">
              <template #tab>
                <span data-test-id="oushudb-worksheet-resource-table-tab-title">
                  基础表
                  <span>{{ table.total }}</span>
                </span>
              </template>
              <x-spin :spinning="!tabsLoading && table.spinning">
                <!-- 搜索 -->
                <div class="work-sheet-sidebar-search">
                  <x-input-search
                    v-model:value="table.searchValue"
                    allow-clear
                    class="raw"
                    data-test-id="oushudb-worksheet-recourse-table-tab-search-input"
                    placeholder="请输入表名，按回车搜索"
                    @search="handleTableOnSearch"
                    @keyup.enter="handleTableOnSearch"
                  />
                </div>
                <!-- 分页 -->
                <a-pagination
                  v-model:current="table.current"
                  :page-size="table.pageSize"
                  :total="table.total"
                  data-test-id="oushudb-worksheet-resource-table-tab-pagination"
                  simple
                  @change="handleTablePaginationChange"
                >
                  <template #itemRender="{ page, type, originalElement }">
                    <template v-if="type === 'prev' || page === ''">
                      <div
                        data-test-id="oushudb-worksheet-resource-table-tab-pagination-first-page-btn"
                        style="display: inline-block; padding-right: 8px;"
                        @click.stop="handleTableJumpToFirstPage"
                      >
                        <a class="antv-pagination-item-link" title="第一页">
                          <icon name="worksheet/arrow_double"/>
                        </a>
                      </div>
                      <a class="antv-pagination-item-link"
                         data-test-id="oushudb-worksheet-resource-table-tab-pagination-previous-page-btn">
                        <icon name="worksheet/arrow"/>
                      </a>
                    </template>
                    <template v-else-if="type === 'next' || page === ''">
                      <a
                        class="antv-pagination-item-link"
                        data-test-id="oushudb-worksheet-resource-table-tab-pagination-next-page-btn"
                      >
                        <icon name="worksheet/arrow"/>
                      </a>
                      <div
                        data-test-id="oushudb-worksheet-resource-table-tab-pagination-last-page-btn"
                        style="display: inline-block; padding-left: 8px;"
                        @click.stop="handleTableJumpToLastPage"
                      >
                        <a class="antv-pagination-item-link" title="最后一页">
                          <icon name="worksheet/arrow_double"/>
                        </a>
                      </div>
                    </template>
                    <renderVNode v-else :vnode="originalElement"></renderVNode>
                  </template>
                </a-pagination>
                <div class="work-sheet-sidebar-tab-list table">
                  <a-collapse v-model:active-key="tableCollapseKeys" :bordered="false">
                    <!-- 箭头 -->
                    <template #expandIcon="{ isActive }">
                      <icon :style="{transform: `rotate(${isActive?'0deg' : '-90deg'})`}" image name="worksheet/arrow"/>
                    </template>
                    <a-collapse-panel v-for="_table in (table.list as any[])" :key="_table.oid">
                      <!-- 表 -->
                      <template #header>
                        <div
                          :class="[_table.ellipsisHover ? 'ellipsis-hover' : '']"
                          class="work-sheet-sidebar-tab-list-item"
                        >
                          <span :title="_table.name">
                            <icon name="worksheet/table_add"/>{{ _table.name }}
                          </span>
                          <span @click.stop="()=>{}">
                            <x-tooltip placement="topLeft" title="编辑">
                              <x-button icon-name="worksheet/edit" type="text"
                                        @click="() => handleEditTableIconClick(_table)"/>
                            </x-tooltip>
                            <x-tooltip placement="topLeft" title="删除">
                              <x-button icon-name="worksheet/delete" type="text"
                                        @click="() => handleDeleteTableIconClick(_table)"/>
                            </x-tooltip>
                            <a-dropdown :key="_table.oid" @visibleChange="(visible: boolean)=>handleTableDropdownVisibleChange(visible, _table)">
                              <x-button icon-name="worksheet/ellipsis" type="text"/>
                              <template #overlay>
                                <x-menu @click="_table.ellipsisHover = false">
                                  <x-menu-item
                                    @click="() => handleRefreshColumnsClick(_table)"
                                  >
                                    <icon image name="worksheet/refresh_black"/>
                                    刷新列
                                  </x-menu-item>
                                  <x-menu-item
                                    :disabled="schemaSelectedName === undefined"
                                    @click="() => handleCopyTableName(_table.name)"
                                  >
                                    <icon color="black" name="worksheet/copy"/>
                                    复制表名
                                  </x-menu-item>
                                  <x-menu-divider/>
                                  <x-menu-item
                                    :disabled="schemaSelectedName === undefined"
                                    @click="() => handleAddColumnIconClick(_table)"
                                  >
                                    <icon color="black" image name="worksheet/column_add"/>
                                    新建列
                                  </x-menu-item>
                                  <x-menu-item
                                    :disabled="schemaSelectedName === undefined"
                                    @click="() => handleShowTableSecondaryIndexClick(_table)"
                                  >
                                    <icon color="black" image name="worksheet/secondary_index"/>
                                    二级索引
                                  </x-menu-item>
                                  <x-menu-item
                                    :disabled="schemaSelectedName === undefined"
                                    @click="() => handleConnectionInfoClick(_table)"
                                  >
                                    <icon color="black" image name="worksheet/connect"/>
                                    关联信息
                                  </x-menu-item>
                                </x-menu>
                              </template>
                            </a-dropdown>
                          </span>
                        </div>
                      </template>
                      <!-- 列 -->
                      <x-spin :spinning="_table.loading" font-size="6px">
                        <div
                          v-for="(column, index) in _table.columns"
                          :key="index"
                          class="work-sheet-sidebar-tab-list-item-children"
                        >
                        <span :title="column.name">
                          <icon image name="worksheet/field_blue"/>{{ column.name }}
                        </span>
                          <span>
                          <x-tooltip placement="bottomLeft" title="编辑列">
                            <x-button icon-name="worksheet/edit" type="text"
                                      @click="() => handleEditColumnIconClick(_table, column)"/>
                          </x-tooltip>
                          <x-tooltip placement="bottomLeft" title="删除列">
                            <x-button icon-name="worksheet/delete" type="text"
                                      @click="() => handleDeleteColumnIconClick(_table, column)"/>
                          </x-tooltip>
                        </span>
                        </div>
                      </x-spin>
                    </a-collapse-panel>
                  </a-collapse>
                </div>
              </x-spin>
            </x-tab-pane>
            <x-tab-pane key="searchTable">
              <template #tab>
                <span data-test-id="oushudb-worksheet-resource-table-tab-title">
                  查询表
                  <span>{{ searchTable.total }}</span>
                </span>
              </template>
              <x-spin :spinning="!tabsLoading && searchTable.spinning">
                <!-- 搜索 -->
                <div class="work-sheet-sidebar-search">
                  <x-input-search
                    v-model:value="searchTable.searchValue"
                    allowClear
                    class="raw"
                    data-test-id="oushudb-worksheet-recourse-search-table-tab-search-input"
                    placeholder="请输入查询表名，按回车搜索"
                    @search="handleSearchTableOnSearch"
                    @keyup.enter="handleSearchTableOnSearch"
                  />
                </div>
                <!-- 分页 -->
                <a-pagination
                  v-model:current="searchTable.current"
                  :page-size="searchTable.pageSize"
                  :total="searchTable.total"
                  data-test-id="oushudb-worksheet-resource-search-table-tab-pagination"
                  simple
                  @change="handleSearchTablePaginationChange"
                >
                  <template #itemRender="{ page, type, originalElement }">
                    <template v-if="type === 'prev' || page === ''">
                      <div
                        data-test-id="oushudb-worksheet-resource-search-table-tab-pagination-first-page-btn"
                        style="display: inline-block; padding-right: 8px;"
                        @click.stop="handleSearchTableJumpToFirstPage"
                      >
                        <a class="antv-pagination-item-link" title="第一页">
                          <icon name="worksheet/arrow_double"/>
                        </a>
                      </div>
                      <a
                        class="antv-pagination-item-link"
                        data-test-id="oushudb-worksheet-resource-search-table-tab-pagination-previous-page-btn"
                      >
                        <icon name="worksheet/arrow"/>
                      </a>
                    </template>
                    <template v-else-if="type === 'next' || page === ''">
                      <a
                        class="antv-pagination-item-link"
                        data-test-id="oushudb-worksheet-resource-search-table-tab-pagination-next-page-btn"
                      >
                        <icon name="worksheet/arrow"/>
                      </a>
                      <div
                        data-test-id="oushudb-worksheet-resource-search-table-tab-pagination-last-page-btn"
                        style="display: inline-block; padding-left: 8px;"
                        @click.stop="handleSearchTableJumpToLastPage"
                      >
                        <a class="antv-pagination-item-link" title="最后一页">
                          <icon name="worksheet/arrow_double"/>
                        </a>
                      </div>
                    </template>
                    <renderVNode v-else :vnode="originalElement"></renderVNode>
                  </template>
                </a-pagination>
                <!--  -->
                <div class="work-sheet-sidebar-tab-list searchTable">
                  <div
                    v-for="(_searchTable, index) in (searchTable.list as any[])" :key="index"
                    :class="[_searchTable.ellipsisHover ? 'ellipsis-hover' : '']"
                    class="work-sheet-sidebar-tab-list-item"
                  >
                    <span :title="_searchTable.name">
                      <icon name="worksheet/search_table"/>{{ _searchTable.name }}
                    </span>
                    <span @click.stop="()=>{}">
                      <x-tooltip placement="bottomLeft" title="编辑">
                        <x-button icon-name="worksheet/edit" type="text" @click="() => handleEditSearchTableIconClick(_searchTable)"/>
                      </x-tooltip>
                      <x-tooltip placement="bottomLeft" title="删除">
                        <x-button icon-name="worksheet/delete" type="text"
                                  @click="() => handleDeleteSearchTableIconClick(_searchTable)"/>
                      </x-tooltip>
                      <a-dropdown @visibleChange="(visible: any) => {_searchTable.ellipsisHover = visible}">
                        <x-button icon-name="worksheet/ellipsis" type="text"/>
                        <template #overlay>
                          <x-menu @click="_searchTable.ellipsisHover = false">
                            <x-menu-item
                              :disabled="schemaSelectedName === undefined"
                              @click="() => handleCopySearchTableName(_searchTable.name)"
                            >
                              <icon color="black" name="worksheet/copy"/>
                              复制表名
                            </x-menu-item>
                            <x-menu-divider/>
                            <x-menu-item @click="() => handlePreviewSearchTableDataClick(_searchTable)">
                              <icon color="black" name="worksheet/secondary_index"/>
                              数据查询
                            </x-menu-item>
                          </x-menu>
                        </template>
                      </a-dropdown>
                    </span>
                  </div>
                </div>
              </x-spin>
            </x-tab-pane>
            <template #tabBarExtraContent>
              <a-dropdown>
                <x-button data-test-id="oushudb-worksheet-resource-table-search-table-function-extra-dropdown" icon-name="worksheet/ellipsis"
                          type="text"/>
                <template #overlay>
                  <x-menu>
                    <template v-if="activeTabKey === 'table'">
                      <x-menu-item
                        :disabled="schemaSelectedName === undefined"
                        data-test-id="oushudb-worksheet-resource-table-refresh-menu-item"
                        @click="handleRefreshTable"
                      >
                        <icon color="inherit" name="worksheet/refresh"/>
                        刷新基础表
                      </x-menu-item>
                      <x-menu-item
                        :disabled="schemaSelectedName === undefined"
                        data-test-id="oushudb-worksheet-resource-table-add-menu-item"
                        @click="handleAddTableIconClick"
                      >
                        <icon color="inherit" name="worksheet/table_add"/>
                        新建基础表
                      </x-menu-item>
                    </template>
                    <template v-if="activeTabKey === 'searchTable'">
                      <x-menu-item
                        :disabled="schemaSelectedName === undefined"
                        data-test-id="oushudb-worksheet-resource-table-refresh-menu-item"
                        @click="handleRefreshSearchTable"
                      >
                        <icon color="inherit" name="worksheet/refresh"/>
                        刷新查询表
                      </x-menu-item>
                      <x-menu-item
                        :disabled="schemaSelectedName === undefined"
                        data-test-id="oushudb-worksheet-resource-table-add-menu-item"
                        @click="handleAddSearchTableIconClick"
                      >
                        <icon color="inherit" name="worksheet/search_table_add"/>
                        新建查询表
                      </x-menu-item>
                    </template>
                  </x-menu>
                </template>
              </a-dropdown>
            </template>
          </x-tabs>
        </x-spin>
      </div>
  </div>

  <!-- （数据库｜模式）（新建｜编辑）-->
  <x-drawer
    title="新建模式"
    :visible="addSchemaDrawerVisible"
    class="v-oushudb-add-db-drawer"
    destroyOnClose
    width="800"
    @close="() => { addSchemaDrawerVisible = false }"
  >
    <addOrUpdateSchema
      :init-already-exist-name-list="schemaList.map(schema => schema.name)"
      @close="(success: any) => {
        addSchemaDrawerVisible=false;
        if (success) handleRefreshSchema()
      }"
    />
  </x-drawer>
  <!-- 表（新建｜编辑） -->
  <x-drawer
    :visible="addOrEditTableDrawerVisible"
    class="v-oushudb-add-table-drawer"
    destroyOnClose
    width="1000"
    @close="() => { addOrEditTableDrawerVisible = false; tableToEdit.ellipsisHover = false; }"
  >
    <template #title>
      {{ `${isAddTable ? '新建' : '编辑'}基础表` }}
      <!-- <x-tooltip v-if="isAddTable" placement="bottomLeft" title="点击查看如何建表">
        <x-button icon-name="worksheet/info_hollow" type="text"
                  @click="windowOpen('http://www.oushu.com/docs/ch/data-definition-tables.html', '_blank')"/>
      </x-tooltip> -->
    </template>
    <AddOrUpdateTable
      :init-already-exist-name-list="table.list.map(item => item.name)"
      :init-table="tableToEdit"
      :is-add="isAddTable"
      :schema="schemaSelectedName"
      @close="(isSuccess: any) => {
        addOrEditTableDrawerVisible = false;
        tableToEdit.ellipsisHover = false;
        if((isAddTable && isSuccess)
        || (!isAddTable && isSuccess
        && schemaSelectedName === tableToEdit.schema))
          handleRefreshTable(isAddTable)
      }"
    />
  </x-drawer>
  <!-- 字段（新建｜编辑） -->
  <AddOrUpdateColumnForExistTable
    v-model:visible="addOrEditColumnDrawerVisible"
    :init-already-exist-name-list="tableInWhichColumnToOperateReside?.columns?.filter(item => item.name !== columnToEdit?.name).map(item => item.name)"
    :initial-column="columnToEdit"
    :is-add="isAddColumn"
    :schema-name="schemaSelectedName"
    :table="tableInWhichColumnToOperateReside"
    @success="() => { handleRefreshColumnsClick(tableInWhichColumnToOperateReside) }"
  />

  <!-- 删除模式 -->
  <x-modal v-model:visible="schemaDeleteModalVisible">
    <template #title>
      <icon class="title-icon" color="danger" name="worksheet/warning"></icon>
      <span>删除模式</span>
    </template>
    <p style="margin-bottom: 30px">删除模式会导致部分资源无法运行，您确认要删除模式 {{ schemaSelectedName }} 吗？</p>
    <x-checkbox v-model:checked="schemaDeleteCascade">同时级联删除</x-checkbox>
    <template #footer>
      <x-button :loading="schemaDeleteLoading" style="width: 100%;" type="primary" @click="handleDeleteSchema">确认删除
      </x-button>
    </template>
  </x-modal>
  <!-- 删除表 -->
  <x-modal v-model:visible="tableDeleteModalVisible">
    <template #title>
      <icon class="title-icon" color="danger" name="worksheet/warning"></icon>
      <span>删除表</span>
    </template>
    <p style="margin-bottom: 30px">删除表会导致部分资源无法运行，您确认要删除表 {{ tableToDelete && tableToDelete.name }} 吗？</p>
    <x-checkbox v-model:checked="tableDeleteCascade">同时级联删除</x-checkbox>
    <template #footer>
      <x-button :loading="tableDeleteLoading" style="width: 100%;" type="primary" @click="handleDeleteTable">确认删除
      </x-button>
    </template>
  </x-modal>
  <!-- 删除字段 -->
  <x-modal v-model:visible="columnToDeleteModalVisible">
    <template #title>
      <icon class="title-icon" color="danger" name="worksheet/warning"></icon>
      <span>删除字段</span>
    </template>
    <p style="margin-bottom: 30px">删除字段会导致部分资源无法运行，您确认要删除字段 {{ columnToDelete && columnToDelete.name }}吗？</p>
    <template #footer>
      <x-button :loading="columnToDeleteLoading" style="width: 100%;" type="primary" @click="handleDeleteColumn">确认删除
      </x-button>
    </template>
  </x-modal>
  <!-- 删除查询表 -->
  <x-modal v-model:visible="searchTableDeleteModalVisible">
    <template #title>
      <icon class="title-icon" color="danger" name="worksheet/warning"></icon>
      <span>删除查询表</span>
    </template>
    <p style="margin-bottom: 30px">删除查询表会导致部分资源无法运行，您确认要删除查询表 {{ searchTableToDelete && searchTableToDelete.name }} 吗？</p>
    <x-checkbox v-model:checked="searchTableDeleteCascade">同时级联删除</x-checkbox>
    <template #footer>
      <x-button :loading="searchTableDeleteLoading" style="width: 100%;" type="primary" @click="handleDeleteSearchTable">确认删除
      </x-button>
    </template>
  </x-modal>

  <!-- 表二级索引抽屉 -->
  <TableSecondaryIndex
    v-model:visible="showTableSecondaryIndex"
    :schema="schemaSelectedName"
    :table="detailTableData"
  />
  <!-- 表关联信息抽屉 -->
  <TableConnectionInfo
    v-model:visible="showTableConnectionInfo"
    :schema="schemaSelectedName"
    :table="detailTableData"
  />

  <!-- 查询表新建 -->
  <x-drawer
    :visible="addSearchTableDrawerVisible"
    class="v-oushudb-add-search-table-drawer"
    destroyOnClose
    title="新建查询表"
    width="1000"
    @close="() => { addSearchTableDrawerVisible = false }"
  >
    <AddSearchTable
      :init-already-exist-name-list="searchTable.list.map(item => item.name)"
      :schema="schemaSelectedName"
      @confirm="handleRefreshSearchTable()"
    />
  </x-drawer>

  <!-- 查询表编辑 -->
  <x-drawer
    :visible="editSearchTableDrawerVisible"
    class="v-oushudb-add-search-table-drawer"
    destroyOnClose
    title="编辑查询表"
    width="1000"
    @close="() => { editSearchTableDrawerVisible = false }"
  >
    <EditSearchTable
      :schema-name="schemaSelectedName"
      :search-table="searchTableToPreview"
      @confirm="handleEditSearchTable"
      @close="() => { editSearchTableDrawerVisible = false }"
    />
  </x-drawer>

  <!-- 查询表数据查询 -->
  <x-drawer
    :visible="previewSearchTableDrawerVisible"
    class="v-oushudb-add-search-table-drawer"
    destroyOnClose
    title="数据查询"
    width="800"
    @close="() => { previewSearchTableDrawerVisible = false }"
  >
    <PreviewSearchTable
      :schema-name="schemaSelectedName"
      :search-table="searchTableToPreview"
      @close="() => { previewSearchTableDrawerVisible = false }"
    />
  </x-drawer>
</template>
<script lang="ts">

/* eslint-disable */
import { computed, ComputedRef, defineComponent, reactive, toRefs, watch, onBeforeMount } from 'vue'
import { useI18n } from 'vue-i18n'
import { translateErrorMessage } from 'lava-fe-lib/lib-common/i18n'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import Icon from '@/smart-ui-vue/helper/Icon.vue'
import { useStore } from 'vuex'
import { message } from 'ant-design-vue-3'
import useClipboard from 'vue-clipboard3'
import { debounce } from 'lodash'
import { isProduction, windowOpen } from '@/smart-ui-vue/utils'
import {
  getColumnList,
  getSchemaList,
  getTableList,
  getSearchTableList,
} from '@/api/mock'
import { ColumnResData, Schema, Table } from '@/components/Worksheet/type'
import AddOrUpdateSchema from '@/components/Worksheet/WorksheetSideBar/addOrUPdateSchema.vue'
import AddOrUpdateTable from '@/components/Worksheet/WorksheetSideBar/addOrUpdateTable.vue'
import AddOrUpdateColumnForExistTable from '@/components/Worksheet/WorksheetSideBar/addOrUpdateColumnForExistTable.vue'
import TableSecondaryIndex from '@/components/Worksheet/WorksheetSideBar/TableDetails/tableSecondaryIndex.vue'
import TableConnectionInfo from '@/components/Worksheet/WorksheetSideBar/TableDetails/tableConnectionInfo.vue'
import AddSearchTable from '@/components/Worksheet/WorksheetSideBar/SearchTable/addSearchTable.vue'
import EditSearchTable from '@/components/Worksheet/WorksheetSideBar/SearchTable/editSearchTable.vue'
import PreviewSearchTable from '@/components/Worksheet/WorksheetSideBar/SearchTable/previewSearchTable.vue'

// @ts-ignore
function renderVNode(_, { attrs: { vnode } }) {
  return vnode
}

export default defineComponent({
  name: 'WorksheetSideBar',
  components: {
    AddOrUpdateSchema,
    AddOrUpdateTable,
    AddOrUpdateColumnForExistTable,
    TableSecondaryIndex,
    TableConnectionInfo,
    AddSearchTable,
    EditSearchTable,
    PreviewSearchTable,
    ...smartUI,
    Icon,
    renderVNode,
  },
  props: {
  },
  setup(props) {
    const { toClipboard } = useClipboard()
    const { t } = useI18n()
    const getError = translateErrorMessage(t)
    const store = useStore()
    const state = reactive({
      sidebarActiveKey: 'resource',
      sidebarHoverKey: '',

      // 模式新建 drawer visible
      addSchemaDrawerVisible: false,

      // 模式选项
      schemaList: [] as Schema[],
      schemaSelectedName: undefined as string | undefined,
      schemaToDelete: undefined as undefined | Schema,
      schemaDeleteModalVisible: false,
      schemaDeleteLoading: false,
      schemaDeleteCascade: false,

      // 基础表新建 or 编辑 drawer visible
      addOrEditTableDrawerVisible: false,
      isAddTable: false,
      tableToEdit: {} as OwnTable,
      tableToDelete: undefined as undefined | OwnTable,
      tableDeleteModalVisible: false,
      tableDeleteLoading: false,
      tableDeleteCascade: false,
      tableCollapseKeys: [] as string[],

      // 字段
      isAddColumn: false,
      addOrEditColumnDrawerVisible: false,
      columnToEdit: undefined as undefined | ColumnResData,
      columnToDelete: undefined as undefined | ColumnResData,
      columnToDeleteModalVisible: false,
      columnToDeleteLoading: false,
      // 对字段进行操作时，记录其所在对 table
      tableInWhichColumnToOperateReside: undefined as undefined | OwnTable,

      // 查询表新建 or 编辑 drawer visible
      addSearchTableDrawerVisible: false,
      editSearchTableDrawerVisible: false,
      previewSearchTableDrawerVisible: false,
      searchTableToPreview: undefined as undefined | ColumnResData,
      searchTableToDelete: undefined as undefined | ColumnResData,
      searchTableDeleteModalVisible: false,
      searchTableDeleteLoading: false,
      searchTableDeleteCascade: false,

      activeTabKey: 'table',
      table: getDefaultTable(),
      searchTable: getDefaultTable(),

      // 每次操作都会重新赋值
      // 选择 schema 时，table 应处于 loading 状态
      tabsLoading: false,
      // 是否显示表二级索引
      showTableSecondaryIndex: false,
      // 是否显示表关联信息
      showTableConnectionInfo: false,
      // 当前表数据
      detailTableData: {} as any,
    })

    const recourseIconName = computed(() => {
      if (state.sidebarActiveKey === 'resource') {
        return 'worksheet/resource_blue_fill'
      } else if (state.sidebarHoverKey === 'resource') {
        return 'worksheet/resource_blue'
      } else {
        return 'worksheet/resource'
      }
    })

    /**
     * 处理复制【链接字符串】
     * @value 待复制到粘贴板的信息
     */
    const handleCopyValue = async(value: any) => {
      try {
        await toClipboard(value)
        message.info('已复制到粘贴板')
      } catch (e) {
        message.error('复制失败，请手动复制')
      }
    }

    // -----------------------------
    // 模式操作
    // -----------------------------
    const handleRefreshSchema = async() => {

      const result = await getSchemaList()

      if (result.meta.success) {
        const META_SCHEMA = [
          'pg_catalog_magma',
          'pg_catalog',
          'pg_bitmapindex',
          'pg_aoseg',
          'information_schema',
          'hawq_toolkit',
        ]
        state.schemaList = (result.data ?? []).filter((schema: any) => !META_SCHEMA.includes(schema.name))
        if (state.schemaList.some(schema => schema.name === state.schemaSelectedName)) {
          // do nothing.
        } else {
          // 有 public 选 public，无则选第一个
          state.schemaSelectedName =
            state.schemaList.find(schema => schema.name === 'public')?.name
            ?? state.schemaList.length ? state.schemaList[0].name : undefined
        }

        // 请求对应的 table、searchTable、function
        if (state.activeTabKey === 'table') {
          state.tabsLoading = true
          await Promise.all([handleGetTableList()])
          state.tabsLoading = false
        }
      } else {
        state.schemaList = []
        message.error(`刷新模式失败：${getError(result)}`)
      }
    }

    /**
     * 模式选中项变化时
     */
    const handleOnSchemaSelectChanged = async() => {
      if (state.schemaSelectedName === undefined || state.schemaSelectedName === '') {
        state.table = getDefaultTable()
        state.searchTable = getDefaultTable()
      } else {
        // store.commit('worksheet/setObjectSchema', {
        //   worksheetId: props.worksheetId,
        //   schema: state.schemaSelectedName
        // })
        // store.commit('worksheet/setObjectDatabaseSymbol', {
        //   worksheetId: props.worksheetId,
        // })
        await Promise.all([handleGetTableList()])
      }
    }

    watch(() => state.schemaSelectedName, handleOnSchemaSelectChanged)

    const handleCopySchemaName = () => {
      handleCopyValue(state.schemaSelectedName)
    }

    /**
     * 新增模式
     */
    const handleAddSchemaIconClick = () => {
      state.addSchemaDrawerVisible = true
    }

    /**
     * 删除模式
     */
    const handleDeleteSchemaIconClick = async(dropCascade: boolean) => {
      state.schemaDeleteModalVisible = true
    }

    const handleDeleteSchema = async() => {
      if (state.schemaSelectedName === undefined) return

      state.schemaDeleteLoading = true

      // 获取删除的SQL语句后，执行SQL

      state.schemaDeleteLoading = false
    }

    // -----------------------------
    // 表操作
    // -----------------------------
    /**
     * 获取表数据
     */
    const handleGetTableList = async() => {
      if (state.schemaSelectedName === undefined) return

      state.table.spinning = true
      const result = await getTableList(
        state.schemaSelectedName,
        state.table.pageSize,
        (state.table.current - 1) * state.table.pageSize,
        state.table.searchValue ? `%${state.table.searchValue}%` : '',
      )

      if (result.meta.success) {
        // state.table.searchValue = ''
        state.tableCollapseKeys = []
        state.table.total = result.data.totalCount
        // todo: 为空时返回的是 null，应返回 []，记得和后端对接
        state.table.list = (result.data.data ?? []).map((table: any) => ({
          ...table,
          loading: false,
        }))
      } else {
        message.error(`刷新表失败：${getError(result)}`)
      }
      state.table.spinning = false
    }

    /**
     * 刷新 table list
     */
    const handleRefreshTable = async(isAddTable?: boolean) => {
      if (isAddTable === undefined)
        state.table.current = 1
      state.table.list = []
      state.table.searchValue = ''

      await handleGetTableList()
      state.tableCollapseKeys = (isAddTable === false && state.tableToEdit.oid) ? [state.tableToEdit.oid] : []
      message.info('刷新表成功')
    }

    /**
     * 新增 table
     */
    const handleAddTableIconClick = () => {
      state.addOrEditTableDrawerVisible = true
      state.isAddTable = true
    }

    /**
     * 编辑table
     */
    const handleEditTableIconClick = (tableToEdit: OwnTable) => {
      state.addOrEditTableDrawerVisible = true
      state.isAddTable = false
      state.tableToEdit = tableToEdit
    }

    /**
     * 删除 table
     */
    const handleDeleteTableIconClick = async(table: OwnTable) => {
      state.tableToDelete = table
      state.tableDeleteModalVisible = true
    }

    const handleDeleteTable = async() => {
      if (!state.tableToDelete) return
      if (state.tableToDelete.schema === '') return

      state.tableDeleteLoading = true

      // 获取删除表的SQL，执行SQL

      state.tableDeleteLoading = false
    }

    /**
     * 复制表名
     */
    const handleCopyTableName = (tableName: string) => {
      handleCopyValue(tableName)
    }

    /**
     * 二级索引
     */
    const handleShowTableSecondaryIndexClick = (table: any) => {
      state.detailTableData = table
      state.showTableSecondaryIndex = true
    }

    /**
     * 关联信息
     */
    const handleConnectionInfoClick = (table: any) => {
      state.detailTableData = table
      state.showTableConnectionInfo = true
    }

    /**
     * table 搜索
     */
    const handleTableOnSearch = debounce(async() => {
      state.table.current = 1
      await handleGetTableList()
    }, 500)

    /**
     * 跳到首页
     */
    const handleTableJumpToFirstPage = () => {
      state.table.current = 1
      handleGetTableList()
    }

    /**
     * 跳到最后一夜
     */
    const handleTableJumpToLastPage = () => {
      state.table.current = Math.ceil(state.table.total / state.table.pageSize)
      handleGetTableList()
    }

    /**
     * 页码改变的回调
     */
    const handleTablePaginationChange = () => {
      console.log('changePage', Date.now())
      handleGetTableList()
    }


    // -----------------------------
    // 列操作
    // -----------------------------
    /**
     * 刷新当前 table 的 columns
     */
    const handleRefreshColumnsClick = async(table: OwnTable | undefined) => {
      if (table === undefined) return

      table.loading = true
      try {
        const result = await getColumnList(
          table.oid,
        )

        if (result.meta.success) {
          table.columns = result.data ?? []
        }
      } catch (e) {
        message.error(`刷新列失败：${e}`)
      } finally {
        table.loading = false
      }
    }

    /**
     * 当前 table 增加 column
     */
    const handleAddColumnIconClick = (table: OwnTable | undefined) => {
      state.tableInWhichColumnToOperateReside = table
      state.columnToEdit = undefined
      state.isAddColumn = true
      state.addOrEditColumnDrawerVisible = true
    }

    /**
     * 当前 table 编辑某一 column
     */
    const handleEditColumnIconClick = (table: OwnTable, column: ColumnResData) => {
      state.tableInWhichColumnToOperateReside = table
      state.columnToEdit = column
      state.isAddColumn = false
      state.addOrEditColumnDrawerVisible = true
    }

    /**
     * 当前 table 删除某一 column
     */
    const handleDeleteColumnIconClick = async(table: OwnTable, column: ColumnResData) => {
      state.tableInWhichColumnToOperateReside = table
      state.columnToDelete = column
      state.columnToDeleteModalVisible = true
    }

    const handleDeleteColumn = async() => {
      if (state.schemaSelectedName === undefined) return
      if (state.columnToDelete === undefined || state.tableInWhichColumnToOperateReside === undefined) return

      state.columnToDeleteLoading = true
      const tableInWhichColumnToOperateReside = state.tableInWhichColumnToOperateReside

      // 获取删除字段SQL，执行SQL
      state.columnToDeleteLoading = false
    }

    watch(() => state.tableCollapseKeys, async(now, pre) => {
      if (now.length > pre.length) {
        const collectedTableKey = now[now.length - 1]
        const table = state.table.list.find(table => table.oid === collectedTableKey)

        if (table === undefined)
          return

        table.loading = true
        try {
          const result = await getColumnList(
            now[now.length - 1],
          )

          if (result.meta.success) {
            table.columns = result.data ?? []
          } else {
            message.error(`获取表${table.name}的列信息失败：${getError(result)}`)
          }
        } catch (e) {
          message.error(`获取表${table.name}的列信息失败：${e}`)
        } finally {
          table.loading = false
        }
      }
    })

    // -----------------------------
    // 查询表操作
    // -----------------------------
    /**
     *  获取 searchTable 数据
     */
    const handleGetSearchTableData = async() => {

      state.searchTable.spinning = true
      const result = await getSearchTableList(
        state.searchTable.pageSize,
        (state.searchTable.current - 1) * state.searchTable.pageSize,
        state.searchTable.searchValue ? `%${state.searchTable.searchValue}%` : '',
      )

      if (result.meta.success) {
        // 全部数据
        // todo: 数据为空时返回的是 null，应为 []
        state.searchTable.list = result.data?.data ?? []
        state.searchTable.total = result.data.totalCount
      } else {
        message.error(`刷新查询表失败：${getError(result)}`)
      }
      state.searchTable.spinning = false
    }

    /**
     * 刷新查询表 list
     */
    const handleRefreshSearchTable = async(isAddTable?: boolean) => {
      if (isAddTable === undefined)
        state.searchTable.current = 1
      state.searchTable.list = []
      state.searchTable.searchValue = ''
      await handleGetSearchTableData()
    }

    /**
     * 复制查询表名
     */
    const handleCopySearchTableName = (searchTableName: string) => {
      handleCopyValue(searchTableName)
    }

    /**
     * 新建查询表点击（弹出 modal or drawer）
     */
    const handleAddSearchTableIconClick = () => {
      state.addSearchTableDrawerVisible = true
    }

    /**
     * 删除当前查询表
     */
    const handleDeleteSearchTableIconClick = async(searchTable: any) => {
      state.searchTableToDelete = searchTable
      state.searchTableDeleteModalVisible = true
    }

    /**
     * 删除查询表
     */
    const handleDeleteSearchTable = () => {
      if (state.schemaSelectedName === undefined) return
      if (!state.searchTableToDelete) return

      state.searchTableDeleteLoading = true

      // 删除

      state.searchTableDeleteLoading = false
    }

    /**
     * 新建 SQL 查询（弹出 modal or drawer）
     */
    const handleAddSQLQueryIconClick = () => {
      message.info('developing～敬请期待～')
    }

    /**
     * 展示查询表结构
     */
    const handleEditSearchTableIconClick = (searchTable: any) => {
      state.searchTableToPreview = searchTable
      state.editSearchTableDrawerVisible = true
    }

    const handleEditSearchTable = (searchTable: any) => {
      console.log(searchTable)
    }

    /**
     * 预览查询表数据
     */
    const handlePreviewSearchTableDataClick = (searchTable: any) => {
      state.searchTableToPreview = searchTable
      state.previewSearchTableDrawerVisible = true
    }

    const handleSearchTableJumpToFirstPage = () => {
      state.searchTable.current = 1
    }

    const handleSearchTableJumpToLastPage = () => {
      state.searchTable.current = Math.ceil(state.searchTable.total / state.searchTable.pageSize)
    }

    const handleSearchTablePaginationChange = (page: number) => {
      // 查询表页面跳转
    }

    const handleSearchTableOnSearch = () => {
      state.searchTable.current = 1
      state.searchTable.searchValue = state.searchTable.searchValue
    }


    onBeforeMount(() => {
      handleRefreshSchema()
      handleRefreshSearchTable()
    })

    return {
      ...toRefs(state),

      // 动态 icon
      recourseIconName,

      windowOpen,

      // 模式操作
      handleRefreshSchema,
      handleOnSchemaSelectChanged,
      handleCopySchemaName,
      handleAddSchemaIconClick,
      handleDeleteSchemaIconClick,
      handleDeleteSchema,

      // 表操作
      handleRefreshTable,
      handleCopyTableName,
      handleAddTableIconClick,
      handleEditTableIconClick,
      handleDeleteTableIconClick,
      handleShowTableSecondaryIndexClick,
      handleConnectionInfoClick,
      handleTablePaginationChange,
      handleTableJumpToFirstPage,
      handleTableJumpToLastPage,
      handleTableOnSearch,
      handleDeleteTable,

      // 列操作
      handleRefreshColumnsClick,
      handleAddColumnIconClick,
      handleEditColumnIconClick,
      handleDeleteColumnIconClick,
      handleDeleteColumn,

      // 查询表 操作
      // 查询表操作
      handleRefreshSearchTable,
      handleCopySearchTableName,
      handleAddSearchTableIconClick,
      handleDeleteSearchTableIconClick,
      handleDeleteSearchTable,
      handleEditSearchTableIconClick,
      handleEditSearchTable,
      handlePreviewSearchTableDataClick,
      handleSearchTableJumpToFirstPage,
      handleSearchTableJumpToLastPage,
      handleSearchTablePaginationChange,
      handleSearchTableOnSearch,
      handleAddSQLQueryIconClick,

      // @ts-ignore
      // 有 bug，记得给 antd 提 issue
      handleTableDropdownVisibleChange: (visible, table) => {
        table.ellipsisHover = visible
      },
    }
  },
})

interface OwnTable extends Table {
  loading: boolean
}

function getDefaultTable() {
  return {
    total: 0,
    current: 1,
    pageSize: 20,
    searchValue: '',
    list: [] as any[],
    spinning: false,
  }
}
</script>
<style lang="scss">
.v-oushudb-work-sheet-sidebar {
  width: 100%;
  min-width: 360px;
  //border-right: 1px solid $color-text-comment;

  .work-sheet-sidebar-title {
    position: relative;
    display: flex;
    // 为什么加上这一行，好像多此一举？
    // 因为：sidebar tabs 宽度塌陷问题（当选择schema 的 table searchTable function 数量为 0 时 触发）
    //width: 461px;
    // 这样可以自适应，但是会有塌陷问题
    width: 100%;
    @include font-normal();
    padding: 5px 0;
    color: $color-primary-black;
    border-bottom: 1px solid $color-line-bold;

    &::before {
      position: absolute;
      top: calc(50% - 10px);
      left: 50%;
      width: 1px;
      height: 20px;
      content: '';
      background-color: $color-line-bold;
    }

    .work-sheet-sidebar-title-resource,
    .work-sheet-sidebar-title-collect {
      display: flex;
      flex: 1 1 50%;
      align-items: center;
      justify-content: center;

      > span {
        cursor: pointer;
      }

      .icon {
        margin-right: 5px;
      }
    }

    .work-sheet-sidebar-title-resource {
      margin-right: 10px;

      & > span.active {
        color: $color-primary-blue;
      }
    }

    .work-sheet-sidebar-title-collect {
      position: relative;
      margin-left: 10px;

      & > span.active {
        color: $color-primary-orange;
      }

      > span > span {
        margin-left: 5px;
        @include font-small();
        color: $color-text-comment;
      }
    }
  }

  .work-sheet-sidebar-main {
    padding-right: 5px;
    padding-left: 5px;
    margin-right: 15px;
    margin-left: 5px;

    .work-sheet-sidebar-selector {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding: 6px 0 0 0;
      color: $color-primary-black;
      gap: 6px;

      .work-sheet-sidebar-selector-item {
        .work-sheet-sidebar-selector-item-select {
          position: relative;
          display: flex;
          align-items: center;
          justify-content: center;

          .smartui-select-with-prefix-icon {
            flex: 1;
            max-width: calc(100% - 28px);
          }

          > .#{$ant-prefix}-btn-icon-only,
          > .icon {
            margin-left: 6px;
          }
        }
      }
    }
  }

  .work-sheet-sidebar-tabs {
    margin-right: 5px;
    margin-left: 5px;

    .smartui-tabs {
      .#{$ant-prefix}-tabs-bar {
        margin-right: 15px;
        margin-bottom: 6px;
        margin-left: 5px;

        .#{$ant-prefix}-tabs-extra-content {
          @include font-normal();
          bottom: 0;
        }

        .#{$ant-prefix}-tabs-tab:not(:last-child) {
          margin-right: 20px;
        }

        .#{$ant-prefix}-tabs-tab {
          padding: 6px 0 10px 0;
          text-shadow: none;

          > span {
            .icon {
              margin-right: 5px;
            }

            > span {
              margin-left: 5px;
              @include font-small();
              color: $color-text-comment;
            }
          }

          &:hover {
            color: $color-primary-blue;
          }
        }
      }

      .#{$ant-prefix}-tabs-tabpane {
        .work-sheet-sidebar-search {
          margin-right: 15px;
          margin-left: 5px;

          .smartui-input {
            @include font-small();
            border-radius: 4px;
            box-shadow: none;

            &:hover, &:focus {
              border-color: $color-primary-blue;
            }

            &.#{$ant-prefix}-input-affix-wrapper-focused {
              border-color: $color-primary-blue;
            }
          }
        }

        .#{$ant-prefix}-pagination {
          //width: 100%;
          display: flex;
          justify-content: left;
          //flex-wrap: wrap;
          max-width: 300px;
          margin: 6px 15px 5px;

          li {
            flex-grow: 1;
          }

          .#{$ant-prefix}-pagination-prev,
          .#{$ant-prefix}-pagination-next {
            cursor: auto;

            .#{$ant-prefix}-pagination-item-link {
              cursor: pointer;

              .icon {
                color: $color-primary-black;
                stroke: none;

                &:hover {
                  color: $color-primary-blue;
                }
              }
            }
          }

          .#{$ant-prefix}-pagination-prev {
            .#{$ant-prefix}-pagination-item-link:nth-child(1) .icon {
              transform: rotate(0);
            }

            .#{$ant-prefix}-pagination-item-link:nth-child(2) .icon {
              transform: rotate(90deg);
            }
          }

          .#{$ant-prefix}-pagination-next {
            .icon {
              transform: rotate(-90deg);
            }

            > div {
              .icon {
                transform: rotate(180deg);
              }
            }
          }

          .#{$ant-prefix}-pagination-disabled {
            .#{$ant-prefix}-pagination-item-link {
              cursor: not-allowed;
              border-color: $color-text-comment !important;
            }

            .icon,
            .icon:hover {
              color: $color-text-comment !important;
            }
          }

          .#{$ant-prefix}-pagination-item,
          .#{$ant-prefix}-pagination-item-link {
            //margin-bottom: 10px;
            //border-color: $color-line-bold;
            transition: all 0s ease;
          }

          .#{$ant-prefix}-pagination-item-link {
            display: inline-block;
            height: 24px;
            padding-right: 7px;
            padding-left: 7px;
            line-height: 24px;
            //border: 1px solid #D5D8DB;

            &:hover {
              border-color: $color-primary-blue;
            }
          }

          .#{$ant-prefix}-pagination-simple-pager {
            display: inline-flex;
            align-items: center;
            height: 24px;
            line-height: 24px;
          }

          .#{$ant-prefix}-pagination-item-active {
            background-color: $color-primary-blue;
            border-color: $color-primary-blue;

            a {
              color: #FFF;
            }
          }
        }

        .work-sheet-sidebar-tab-list {
          .#{$ant-prefix}-collapse-arrow {
            top: calc(50% - 8px);
            left: 15px;
            color: $color-text-comment;
          }

          .#{$ant-prefix}-collapse-header,
          .#{$ant-prefix}-collapse-content,
          .#{$ant-prefix}-collapse-content-box {
            padding: 0;
          }

          // cover: collapse 的 arrow hover
          .#{$ant-prefix}-collapse-header:hover {
            .work-sheet-sidebar-tab-list-item {
              border: 1px solid $color-line-bold;

              > span:nth-of-type(2) {
                opacity: 1;
              }
            }
          }

          .#{$ant-prefix}-collapse-borderless {
            height: calc(100vh - 250px);
            // todo: 记得改回来
            overflow: auto;
            //height: calc(100vh - 250px - 80px - 51px - 10px);
            background-color: #FFF;

            .#{$ant-prefix}-collapse-item {
              border: 0;
            }
          }

          .work-sheet-sidebar-tab-list {

          }

          .work-sheet-sidebar-tab-list-item,
          .work-sheet-sidebar-tab-list-item-children {
            display: flex;
            align-items: center;
            justify-content: space-between;
            white-space: nowrap;
            @include font-normal();
            border: 1px solid transparent;
            border-radius: 4px;

            > span:nth-of-type(1) {
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;

              .icon {
                margin-right: 5px;
              }
            }

            > span:nth-of-type(2) {
              opacity: 0;

              > .#{$ant-prefix}-btn-icon-only {
                margin-left: 2px;
              }
            }

            &:hover,
            &:focus,
            &.ellipsis-hover {
              border: 1px solid $color-line-bold;

              > span:nth-of-type(2) {
                opacity: 1;
              }
            }
          }

          .work-sheet-sidebar-tab-list-item {
            padding: 2px 15px 2px 38px;
          }

          &.searchTable,
          &.dbFunction {
            // todo: 记得改回来
            height: calc(100vh - 250px);
            //height: calc(100vh - 250px - 80px - 51px - 10px);
            overflow: auto;

            .work-sheet-sidebar-tab-list-item {
              padding-left: 15px;
            }
          }

          .work-sheet-sidebar-tab-list-item-children {
            padding: 2px 15px 2px 59px;
          }
        }
      }
    }
  }
}

// copy 一份，直接全拉出来样式有变化，暂时先这样
.work-sheet-sidebar-tab-list.collect-table {
  max-height: calc(100vh - 200px);
  margin-top: 10px;
  margin-right: 15px;
  margin-left: 15px;
  overflow: auto;

  .#{$ant-prefix}-collapse-arrow {
    top: calc(50% - 8px) !important;
    left: 15px !important;
    color: $color-text-comment !important;
  }

  .#{$ant-prefix}-collapse-header,
  .#{$ant-prefix}-collapse-content,
  .#{$ant-prefix}-collapse-content-box {
    padding: 0;
  }

  // cover: collapse 的 arrow hover
  .#{$ant-prefix}-collapse-header:hover {
    .work-sheet-sidebar-tab-list-item {
      border: 1px solid $color-line-bold;

      > span:nth-of-type(2) {
        opacity: 1;
      }
    }
  }

  .#{$ant-prefix}-collapse-borderless {
    height: calc(100vh - 250px - 120px);
    // todo: 记得改回来
    overflow: auto;
    //height: calc(100vh - 250px - 80px - 51px - 10px);
    background-color: #FFF;

    .#{$ant-prefix}-collapse-item {
      border: 0;
    }
  }

  .work-sheet-sidebar-tab-list {

  }

  .work-sheet-sidebar-tab-list-item,
  .work-sheet-sidebar-tab-list-item-children {
    display: flex;
    align-items: center;
    justify-content: space-between;
    white-space: nowrap;
    @include font-normal();
    border: 1px solid transparent;
    border-radius: 4px;

    > span:nth-of-type(1) {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      .icon {
        margin-right: 5px;
      }

      // 收藏：表名后的数据库名/模式名
      > span {
        color: $color-text-comment;
      }
    }

    > span:nth-of-type(2) {
      opacity: 0;

      > .#{$ant-prefix}-btn-icon-only {
        margin-left: 2px;
      }
    }

    &:hover,
    &:focus,
    &.ellipsis-hover {
      border: 1px solid $color-line-bold;

      > span:nth-of-type(2) {
        opacity: 1;
      }
    }
  }

  .work-sheet-sidebar-tab-list-item {
    padding: 2px 15px 2px 38px;
  }

  .work-sheet-sidebar-tab-list-item-children {
    padding: 2px 15px 2px 59px;
  }
}

.cursor-pointer {
  cursor: pointer;
}

.v-oushudb-worksheet-tabs-grow {
  .v-oushudb-work-sheet-sidebar {
    .work-sheet-sidebar-tabs {
      .smartui-tabs {
        .#{$ant-prefix}-tabs-tabpane {
          .work-sheet-sidebar-tab-list {
            .#{$ant-prefix}-collapse-borderless {
              height: calc(100vh - 250px - 40px);
            }

            &.searchTable,
            &.dbFunction {
              height: calc(100vh - 250px - 40px);
            }
          }
        }
      }
    }
  }

  // copy 一份，直接全拉出来样式有变化，暂时先这样
  .work-sheet-sidebar-tab-list.collect-table {
    max-height: calc(100vh - 120px);

    .#{$ant-prefix}-collapse-borderless {
      height: calc(100vh - 250px - 40px);
    }

    &.searchTable,
    &.dbFunction {
      height: calc(100vh - 250px - 40px);
    }
  }
}
</style>
