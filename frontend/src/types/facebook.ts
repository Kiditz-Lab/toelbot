export interface FacebookPage {
  id: string;
  name: string;
  category: string;
  access_token: string;
  picture: Picture;
}

export interface Picture {
  data: PictureData;
}

export interface PictureData {
  url: string;
}
