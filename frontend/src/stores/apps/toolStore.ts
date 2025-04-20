import { useApi } from '@/composables/useApi';
import { router } from '@/router';
import type { McpConnectCommand, TestTool, TestToolResult, Tool, Tools } from '@/types/tool';
import { defineStore } from 'pinia';
import { computed, onBeforeMount, ref, toRefs, watch, watchEffect } from 'vue';
import { useSnackbarStore } from '../snackbarStore';
import { useRoute } from 'vue-router';
import { useAgentStore } from './agentStore';
import type { McpServer } from '@/types/mcp_server';

export const useToolStore = defineStore(
  'toolStore',
  () => {
    const tools = ref<Tools[]>([]);
    const { agent } = toRefs(useAgentStore());
    const { fetchAgent } = useAgentStore();
    const agentTools = computed(() => agent.value?.tools ?? []);
    const selectedTool = ref<Tools>({} as Tools);
    const { showSnackbar } = useSnackbarStore();
    const api = useApi();
    const loading = computed(() => api.loading);
    const disableTestConnection = ref(true);
    const route = useRoute();
    const argsValues = ref<Record<string, string>>({});
    const fetchTools = async () => {
      const response = await api.get<Tools[]>('/tools', { sort: 'name,asc' });
      tools.value = response;
    };
    const toolId = ref('');

    const envValues = ref<Record<string, string | number>>({});
    const selectedToolNames = ref<string[]>([]);
    const selectedToolObjects = computed(() => {
      const tools = selectedTool.value.tools;
      const toolNames = selectedToolNames.value;

      if (!Array.isArray(tools) || !Array.isArray(toolNames)) {
        return [];
      }

      return tools.filter((tool) => toolNames.includes(tool.name));
    });

    watchEffect(() => {
      const env = selectedTool.value?.env;
      if (env && typeof env === 'object') {
        for (const key of Object.keys(env)) {
          if (!(key in envValues.value)) {
            envValues.value[key] = '';
          }
        }
      }
    });

    watch(
      envValues,
      () => {
        disableTestConnection.value = true;
      },
      { deep: true }
    );

    const fetchMcp = async (toolsId: string) => {
      try {
        if (!agentTools.value.includes(toolsId)) {
          return;
        }
        const response = await api.get<McpServer>(`/mcp/agent/${agent.value?.id}/tools/${toolsId}`);
        envValues.value = response.env;
        const usedTools = response.usedTools;
        if (usedTools?.length) {
          usedTools.forEach((usedTool) => {
            const tool = selectedTool.value.tools.find((t) => t.name === usedTool.name);
            if (tool) {
              tool.description = usedTool.description;
            }
          });
        }
        selectedToolNames.value = usedTools.map((tool) => tool.name);
        disableTestConnection.value = false;
      } catch (error) {
        console.error('Error fetching MCP server:', error);
      }
    };
    const findToolById = async (toolsId: string) => {
      try {
        const tool = await api.get<Tools>(`/tools/${toolsId}`);
        selectedTool.value = tool;
        selectedTool.value.tools.forEach(initToolFormData);
        disableTestConnection.value = !agentTools.value.includes(tool.id);
      } catch (error) {
        console.error('Error fetching MCP server:', error);
      }
    };

    onBeforeMount(async () => {
      const route = useRoute();
      const id = route.params.id as string;
      await fetchAgent(id);
    });

    const selectTool = async (info: Tools) => {
      selectedTool.value = info;
      toolId.value = info.id;
      router.push({ name: 'ToolDetail', params: route.params });
    };

    const initToolFormData = (tool: Tool) => {
      if (!tool.formData) {
        tool.formData = {};
        tool.isValid = true;
      }
      tool.testResult = undefined;
      const properties = tool.inputSchema?.properties ?? {};
      for (const [key, prop] of Object.entries(properties)) {
        if (!(key in tool.formData)) {
          tool.formData[key] = prop.default !== undefined ? prop.default : prop.type === 'integer' ? 0 : '';
        }
      }
    };
    const getFields = (tool: Tool) => {
      const properties = tool.inputSchema.properties;
      const required = tool.inputSchema.required || [];
      return Object.entries(properties).map(([key, val]) => ({
        name: key,
        title: val.title || key,
        type: val.type || 'string',
        required: required.includes(key)
      }));
    };

    const testConnection = async (tool: Tool) => {
      const testTool = {
        env: envValues.value,
        name: tool.name,
        command: selectedTool.value.command,
        args: selectedTool.value.args,
        params: tool.formData
      } as TestTool;
      tool.testResult = undefined;
      try {
        const res = await api.post<TestToolResult>('/tools/test', testTool);
        if (res?.isError) {
          const errorMessage = res.content?.[0]?.text || 'An unknown error occurred.';
          showSnackbar(errorMessage, 'error', 3000, 'Error');
          disableTestConnection.value = true;
        } else {
          tool.testResult = res;
          showSnackbar('Tool tested successfully.', 'success', 3000, 'Success');
          disableTestConnection.value = false;
        }
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (error) {
        tool.testResult = { isError: true, content: [{ text: 'Tool tested failed.', type: 'text' }] };
        showSnackbar('Tool tested failed.', 'error', 3000, 'Error');
        disableTestConnection.value = false;
      }
    };

    const saveConnection = async () => {
      const usedTools = selectedToolObjects.value.map((tool) => ({ name: tool.name, description: tool.description }));
      const connectionRequest = {
        args: selectedTool.value.args,
        command: selectedTool.value.command,
        env: envValues.value,
        serverName: selectedTool.value.name,
        toolsId: selectedTool.value.id,
        usedTools
      } as McpConnectCommand;

      const agentId = route.params.id;
      await api.post(`/mcp/agent/${agentId}/tools`, connectionRequest);
      showSnackbar('Tools saved successfully.', 'success', 3000, 'Success');
      router.back();
    };

    const updateConnection = async () => {
      const usedTools = selectedToolObjects.value.map((tool) => ({ name: tool.name, description: tool.description }));
      const agentId = route.params.id;
      await api.put(`/mcp/agent/${agentId}/tools/${selectedTool.value.id}/env`, { usedTools, env: envValues.value } as McpConnectCommand);
      showSnackbar('Tool updated successfully.', 'success', 3000, 'Success');
      router.back();
    };

    const deleteConnection = async () => {
      console.log(envValues.value);
      const agentId = route.params.id;
      await api.del(`/mcp/agent/${agentId}/tools/${selectedTool.value.id}`);
      showSnackbar('Tool removed successfully.', 'success', 3000, 'Success');
      router.back();
    };

    return {
      tools,
      loading,
      fetchTools,
      selectTool,
      selectedTool,
      envValues,
      saveConnection,
      disableTestConnection,
      testConnection,
      getFields,
      agentTools,
      updateConnection,
      deleteConnection,
      argsValues,
      fetchMcp,
      findToolById,
      toolId,
      selectedToolNames,
      selectedToolObjects
    };
  },
  {
    persist: {
      storage: sessionStorage,
      pick: ['disableTestConnection', 'tools', 'selectedTool']
    }
  }
);
