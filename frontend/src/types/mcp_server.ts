import type { UsedTool } from './tool';

export interface McpServer {
  serverName: string;
  command: string;
  args: string[];
  env: Record<string, string>;
  usedTools: UsedTool[];
}
