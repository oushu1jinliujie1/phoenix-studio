<template>
  <div class="editor-component-container">

    <div :id="editorContainerId" ref="container" :data-test-id="`container`"
         class="editor-container"/>
    <!-- 操作栏，别名工具箱 -->
    <div :class="[toolKitExpand ? 'expand' : '']" class="editor-operation-group">
      <x-tooltip :title="`撤销（${IS_MAC ? 'Command + Z' : 'Ctrl + Z'}）`" placement="bottomLeft">
        <x-button
          :style="{ color: COLOR_PRIMARY_BLUE}"
          class="operation-icon"
          icon-name="worksheet/undo"
          type="text"
          @click="handleUndo"
        />
      </x-tooltip>
      <x-tooltip :title="`重做（${IS_MAC ? 'Command + Shift + Z' : 'Ctrl + Shift + Z'}）`" placement="bottomLeft">
        <x-button
          :style="{ color: COLOR_PRIMARY_BLUE}"
          class="operation-icon"
          icon-name="worksheet/redo"
          type="text"
          @click="handleRedo"
        />
      </x-tooltip>
      <x-tooltip :title="`格式化（${IS_MAC ? 'Command + Shift + Z' : 'Ctrl + Shift + Z'}）`" placement="bottomLeft">
        <x-button
          :style="{ color: COLOR_PRIMARY_BLUE}"
          class="operation-icon"
          icon-name="worksheet/format"
          type="text"
          @click="handleFormatCode"
        />
      </x-tooltip>
      <x-tooltip :title="`块注释（${IS_MAC ? 'Command + , + /' : 'Ctrl + , + /'}）`" placement="bottomLeft">
        <x-button
          :style="{ color: COLOR_PRIMARY_BLUE}"
          class="operation-icon"
          icon-name="worksheet/comment"
          type="text"
          @click="handleBlockComment"
        />
      </x-tooltip>
      <x-tooltip placement="bottomLeft" title="快捷键">
        <x-button
          :style="{ color: COLOR_PRIMARY_BLUE}"
          class="operation-icon"
          icon-name="worksheet/shortcut"
          type="text"
          @click="() => { shortcutsDrawerVisible = true }"
        />
      </x-tooltip>
    </div>

    <x-drawer v-model:visible="shortcutsDrawerVisible" fixed title="快捷键" width="800">
      <div class="editor-component-container-shortcuts-container" style="display: flex;justify-content: space-between">
        <div class="editor-component-container-shortcuts-column-1" style="width: 340px;">
          <x-row v-for="(shortcut, index) of SHORTCUTS[0]" :key="index"
                 class="editor-component-container-shortcuts-row">
            <x-col :span="12" class="editor-component-container-shortcut">
              <span v-for="(item, index) of (IS_MAC ? shortcut.mac: shortcut.winOrLinux)" :key="index">
                {{ item }}
              </span>
            </x-col>
            <x-col :span="12" class="editor-component-container-shortcut-description">{{ shortcut.description }}</x-col>
          </x-row>
        </div>
        <div class="editor-component-container-shortcuts-column-2">
          <x-row v-for="(shortcut, index) of SHORTCUTS[1]" :key="index"
                 class="editor-component-container-shortcuts-row">
            <x-col :span="12" class="editor-component-container-shortcut">
              <span v-for="(item, index) of (IS_MAC ? shortcut.mac: shortcut.winOrLinux)" :key="index">
                {{ item }}
              </span>
            </x-col>
            <x-col :span="12" class="editor-component-container-shortcut-description">{{ shortcut.description }}</x-col>
          </x-row>
        </div>
      </div>
    </x-drawer>
  </div>
</template>
<script lang="ts">
/* eslint-disable */
import {
  computed,
  ComputedRef,
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  toRefs,
  watch,
} from 'vue'
import * as monaco from 'monaco-editor'
import { SQL_STR } from '../constant'
import { useStore } from 'vuex'
import Icon from '../../Icon.vue'
// @ts-ignore
import smartUI from '@/smart-ui-vue/index.js'
import { message } from 'ant-design-vue-3'
import { debounce } from 'lodash'
import dayjs from 'dayjs'
import { format } from 'sql-formatter'
import { COLOR_PRIMARY_BLUE } from 'lava-fe-lib/lib-common/constant'
import { editor } from 'monaco-editor'
import { uuid } from '@/smart-ui-vue/utils'

import('monaco-themes/themes/Tomorrow.json')
  .then(data => {
    // @ts-ignore
    monaco.editor.defineTheme('Tomorrow', data)
  })


// SQL 代码补全
monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: function(model, position) {
    // 获取当前行数
    const line = position.lineNumber

    // 获取当前列数
    const column = position.column
    // 获取当前输入行的所有内容
    const content = model.getLineContent(line)
    // 暂时没用，留着后续做扩展
    // 通过下标来获取当前光标后一个内容，即为刚输入的内容
    const sym = content[column - 2]
    // const textUntilPosition = model.getValueInRange({
    //   startLineNumber: 1,
    //   startColumn: 1,
    //   endLineNumber: position.lineNumber,
    //   endColumn: position.column,
    // })
    const word = model.getWordUntilPosition(position)
    const range = {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: word.startColumn,
      endColumn: word.endColumn,
    }
    /* ---------------------------------------------------
      上面的代码仅仅是为了获取sym，即提示符
      --------------------------------------------------- */
    const suggestions = []
    // if (sym === '$') {
    //   /* ...
    //     拦截到用户输入$，开始设置提示内容，同else中代码一致，自行拓展 */
    // } else {
    // 直接提示，以下为sql语句关键词提示
    for (const i in SQL_STR) {
      suggestions.push({
        label: SQL_STR[i].label, // 显示的提示内容
        kind: monaco.languages.CompletionItemKind.Function, // 用来显示提示内容后的不同的图标
        insertText: SQL_STR[i].label, // 选择后粘贴到编辑器中的文字
        detail: SQL_STR[i].detail, // 提示内容后的说明
        range: range,
      })
    }
    // }
    return {
      suggestions: suggestions,
    }
  },
  triggerCharacters: [''],
})

// SQL 格式化
monaco.languages.registerDocumentFormattingEditProvider('sql', {
  provideDocumentFormattingEdits(model) {
    return [{
      text: format(model.getValue(), {
        language: 'postgresql',
      }),
      range: model.getFullModelRange(),
    }]
  },
})


interface Shortcut {
  description: string
  winOrLinux: string[]
  mac: string[]
}

const SHORTCUTS: Shortcut[][] = [
  [
    {
      'description': '保存至版本历史',
      'winOrLinux': ['Ctrl', 'S'],
      'mac': ['Command', 'S'],
    },
    {
      'description': '撤销',
      'winOrLinux': ['Ctrl', 'Z'],
      'mac': ['Command', 'Z'],
    },
    {
      'description': '重做',
      'winOrLinux': ['Ctrl', 'Shift', 'Z'],
      'mac': ['Command', 'Shift', 'Z'],
    },
    {
      'description': '搜索',
      'winOrLinux': ['Ctrl', 'F'],
      'mac': ['Command', 'F'],
    },
    {
      'description': '格式化',
      'winOrLinux': ['Ctrl', 'Alt', 'L'],
      'mac': ['Command', 'Alt', 'L'],
    },
    {
      'description': '全选',
      'winOrLinux': ['Ctrl', 'A'],
      'mac': ['Command', 'A'],
    },
    {
      'description': '添加缩进',
      'winOrLinux': ['Tab'],
      'mac': ['Tab'],
    },
    {
      'description': '减少缩进',
      'winOrLinux': ['Shift + Tab'],
      'mac': ['Shift', 'Tab'],
    },
    {
      'description': '行注释',
      'winOrLinux': ['Ctrl', '/'],
      'mac': ['Command', '/'],
    },
    {
      'description': '块注释',
      'winOrLinux': ['Ctrl', 'Alt', '/'],
      'mac': ['Command', 'Option', '/'],
    },
    {
      'description': '增大字体',
      'winOrLinux': ['Ctrl', '='],
      'mac': ['Command', '='],
    },
    {
      'description': '减小字体',
      'winOrLinux': ['Ctrl', '-'],
      'mac': ['Command', '-'],
    },
  ],
  // ↑ ← →
  [
    {
      'description': '下移当前行',
      'winOrLinux': ['Alt', '↓'],
      'mac': ['Option', '↓'],
    },
    {
      'description': '上移当前行',
      'winOrLinux': ['Alt', '↑'],
      'mac': ['Option', '↑'],
    },

    {
      'description': '向下选择',
      'winOrLinux': ['Shift', '↓'],
      'mac': ['Shift', '↓'],
    },
    {
      'description': '向右选择',
      'winOrLinux': ['Shift', '→'],
      'mac': ['Shift', '→'],
    },
    {
      'description': '向左选择',
      'winOrLinux': ['Shift', '←'],
      'mac': ['Shift', '←'],
    },
    {
      'description': '向上选择',
      'winOrLinux': ['Shift', '↑'],
      'mac': ['Shift', '↑'],
    },
    {
      'description': '选择至行尾',
      'winOrLinux': ['Alt', 'Shift', '→'],
      'mac': ['Option', 'Shift', '→'],
    },
    {
      'description': '选择至行头',
      'winOrLinux': ['Alt', 'Shift', '←'],
      'mac': ['Option', 'Shift', '←'],
    },
    {
      'description': '光标跳转至文本右侧',
      'winOrLinux': ['Ctrl', 'Shift', '→'],
      'mac': ['Option', '→'],
    },
    {
      'description': '光标跳转至文本左侧',
      'winOrLinux': ['Ctrl', 'Shift', '←'],
      'mac': ['Option', '←'],
    },
    {
      'description': '往上增加光标',
      'winOrLinux': ['Ctrl', 'Alt', '↑'],
      'mac': ['Command', 'Option', '↑'],
    },
    {
      'description': '往下增加光标',
      'winOrLinux': ['Ctrl', 'Alt', '↓'],
      'mac': ['Command', 'Option', '↓'],
    }],
]
export default defineComponent({
  name: 'Editor',
  components: { Icon, ...smartUI },
  props: {
  },
  setup(props) {
    const store = useStore()
    const state = reactive({
      // 工具箱展开轧辊台
      toolKitExpand: true,
      // 当前 SQL 版本 Id
      SQLId: -1,
      // 保存 SQL 版本
      saveSQL: {
        modalVisible: false,
        SQLVersionName: '',
      },
      // 手动保存确认按钮 loading 状态
      confirmLoading: false,
    })

    const editorContainerId = `container-${uuid()}`

    const shortcutsDrawerVisible = ref<boolean>(false)
    const worksheetInfoRef: ComputedRef<any | undefined> = computed(() => store.getters['worksheet/getWorkSheet'] || {})

    /**
     * editor 初始化配置
     */
    const initEditor = () => {
      const domEditor = document.getElementById(editorContainerId)
      // 初始化配置
      if (domEditor === null) {
        console.log('编辑器初始化失败，请刷新重试')
        return
      }
      editor = monaco.editor.create(domEditor, {
        fontSize: 14,
        theme: 'Tomorrow',
        readOnly: false,
        automaticLayout: true,
        language: 'sql',
        value: '',
        minimap: {
          enabled: false,
        },
        lineNumbers: 'on',
      })
      editor.trigger('editor', 'editor.action.formatDocument', null)
      /**
       * 文本改变时，触发自动保存
        */
      const editorModel = editor.getModel()
      if (editorModel) {
        editorModel.onDidChangeContent((event: editor.IModelContentChangedEvent) => {
          if (editor === null)
            return

          const SQLText = editor.getValue()

          // 老变更文本，暂时保留
          store.commit('worksheet/setSQLInfo', {
            SQLText,
            SQL: {
              SQLText,
              SQL: state.SQLId,
            },
          })

          // 新变更文本
          store.commit('worksheet/setSQLText', {
            SQLText,
          })
        })
      } else {
        console.error('editor model 获取失败，请排查！')
      }

      // 光标选中改变回调事件
      editor.onDidChangeCursorSelection(() => {
        if (editor === null)
          return

        const selection = editor.getSelection()
        if (selection === null)
          return

        // 上传选中文本
        store.commit('worksheet/setSQLSelectedText', {
          selectedText: editor.getModel()?.getValueInRange(selection),
        })
      })

      // 查看快捷键
      editor.addAction({
        id: 'show-shortcuts',
        label: '查看快捷键',
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1,
        run: () => {
          shortcutsDrawerVisible.value = true
        },
      })

      // 添加手动保存命令
      editor.addAction({
        id: 'handle-save',
        label: '保存至版本历史',
        keybindings: [
          monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS,
        ],
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1.5,
        run: () => {
          handleOpenSaveModal()
        },
      })

      // 格式化
      editor.addAction({
        id: 'formatter',
        label: '格式化文档（快捷键2）',
        keybindings: [
          monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KeyL,
        ],
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1.5,
        run: () => {
          editor?.getAction('editor.action.formatDocument').run()
        },
      })

      // 块注释
      editor.addAction({
        id: 'block-comment',
        label: '块注释',
        keybindings: [
          monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.Slash,
        ],
        run: () => {
          editor?.getAction('editor.action.blockComment').run()
        },
      })

      // 放大
      editor.addAction({
        id: 'zoom-in',
        label: '增大字体',
        keybindings: [
          monaco.KeyMod.CtrlCmd | monaco.KeyCode.Equal,
        ],
        run: () => {
          editor?.getAction('editor.action.fontZoomIn').run()
        },
      })

      // 缩小
      editor.addAction({
        id: 'zoom-out',
        label: '缩小字体',
        keybindings: [
          monaco.KeyMod.CtrlCmd | monaco.KeyCode.Minus,
        ],
        run: () => {
          editor?.getAction('editor.action.fontZoomOut').run()
        },
      })

      return editor
    }

    let editor: monaco.editor.IStandaloneCodeEditor | null = null

    const handleSwitchExpand = () => {
      state.toolKitExpand = !state.toolKitExpand
    }

    const handleOpenSaveModal = () => {
      state.saveSQL.modalVisible = true
    }

    const handleCloseSaveModal = () => {
      state.saveSQL.modalVisible = false
      state.saveSQL.SQLVersionName = ''
    }

    const handleFormatCode = () => {
      editor?.getAction('editor.action.formatDocument').run()
    }

    const handleBlockComment = () => {
      editor?.getAction('editor.action.blockComment').run()
    }

    const handleUndo = () => {
      // @ts-ignore
      editor?.getModel()?.undo()
    }

    const handleRedo = () => {
      // @ts-ignore
      editor?.getModel()?.redo()
    }

    /**
     * 用户从SQL文件导入SQL
     * value: 文件名（注释） + 换行符 + 文件内容
     */
    watch(() => worksheetInfoRef.value?.newSQL?.SQLTextFromImportSymbol, (value) => {
      if (!editor) {
        console.error('获取不到 editor 对象，请排查')
        return
      }
      const curValue = editor.getValue()
      // 没有换行符
      if (curValue.trim() === '' && curValue.indexOf('\n') === -1)
        editor.setValue(worksheetInfoRef.value?.newSQL?.SQLTextFromImport ?? '')
      else
        editor.setValue(curValue + '\n\n' + worksheetInfoRef.value?.newSQL?.SQLTextFromImport ?? '')
    })

    onMounted(() => {
      initEditor()
    })

    onUnmounted(() => {
      editor?.dispose()
    })

    return {
      editorContainerId,
      SHORTCUTS,
      COLOR_PRIMARY_BLUE,
      IS_MAC: /macintosh|mac os x/i.test(navigator.userAgent),

      ...toRefs(state),

      shortcutsDrawerVisible,

      handleSwitchExpand,
      handleOpenSaveModal,
      handleCloseSaveModal,
      handleFormatCode,
      handleBlockComment,
      handleUndo,
      handleRedo,
    }
  },
})
</script>
<style lang="scss">
// editor 最外层的 div
.editor-component-container {
  position: relative;
  width: 100%;
  height: calc(100% - 40px);
  padding-left: 1px;

  // editor 挂载的 div
  .editor-container {
    height: 100%;

    // 隐藏：右侧的灰线以及 cursor 所在行的指示
    .decorationsOverviewRuler {
      display: none;
    }
  }

  // editor 左下角的工具箱
  .editor-operation-group {
    position: absolute;
    top: -37px;
    right: 96px;
    display: inline-flex;
    overflow: hidden;
    line-height: 1;
    border-radius: 4px;
    transition: all .4s ease;

    .operation-icon {
      flex-shrink: 0;
      margin-right: 12px;

      &:last-child {
        margin-right: 0;
      }
    }
  }
}

.worksheet-main {
  .editor-operation-group {
    right: 54px;
  }
}

.editor-component-container-shortcuts-container {
  display: flex;
  justify-content: space-between;

  .editor-component-container-shortcuts-column-1,
  .editor-component-container-shortcuts-column-2 {
    width: 340px;

    .editor-component-container-shortcuts-row {
      @include font-normal();
      height: 32px;

      border-bottom: 1px solid $color-line-bold;

      &:first-child {
        border-top: 1px solid $color-line-bold;
      }

      .editor-component-container-shortcut {
        display: flex;
        align-items: center;

        > span {
          display: inline-block;
          min-width: 16px;
          height: 16px;
          padding: 0 4px;
          line-height: 16px;
          color: $color-primary-black;
          background-color: rgba($color-line-bold, .5);
          border-radius: 2px;

          &:not(:last-child) {
            margin-right: 5px;
          }
        }
      }

      .editor-component-container-shortcut-description {
        display: flex;
        align-items: center;
        padding: 0 20px;
        color: $color-text-comment;
      }
    }
  }
}
</style>
