export interface Agent {
    id: string;
    name: string;
    public: boolean
    agentKey: string;
    config: {
      aiModel: string;
      prompt: string;
      vendor: string;
      temperature: number;
    };
    timing: {
      updatedAt: string;
      createdAt: string;
    };
    version: number;
    createdBy: string;
    tools: string[];
    facebooks: string[];
  }