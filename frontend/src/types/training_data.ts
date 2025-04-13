export interface TrainingData {
    id: string
    agentId: string
    url: string
    size: number
    content: string
    status: string
    type: string
    createdBy: string
    progress: boolean
    website: Website
  }

  export interface Website {
    title: string
    description: string
    image: string
  }
  