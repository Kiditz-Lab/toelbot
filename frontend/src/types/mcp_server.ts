export interface McpServer {
  serverName: string;
  command: string;
  args: string[];
  env: Record<string, string>;
}
