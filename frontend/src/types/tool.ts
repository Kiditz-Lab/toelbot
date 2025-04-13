export interface ToolList {
  id: string;
  name: string;
  imageUrl: string;
  description: string;
  shortDescription: string;
}

export interface Tools {
  id: string;
  name: string;
  command: string;
  imageUrl: string;
  description: string;
  shortDescription: string;
  tools: Tool[];
  args: string[];
  env: Record<string, string>;
}

export interface Tool {
  name: string;
  isValid: boolean;
  id: string;
  description: string;
  inputSchema: {
    type: 'object';
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    properties: Record<string, any>;
    required?: string[];
  };

  formData?: Record<string, unknown>; 
}

export interface TestTool {
  
  command: string;
  args: string[];
  name: string;
  env: Record<string, string>;
  params: Record<string, string>;
}

export interface TestToolResult {
  content: Content[]
  isError: boolean
}

export interface Content {
  type: string
  text: string
}

export interface McpConnectCommand {
  toolsId: string
  serverName: string
  command: string
  args: string[]
  env: Record<string, string>
}

export interface ParsedArgField {
  key: string;
  type: string;
  value?: string;
}
