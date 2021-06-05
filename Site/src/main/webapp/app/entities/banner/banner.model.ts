import * as dayjs from 'dayjs';

export interface IBanner {
  id?: number;
  imagenContentType?: string | null;
  imagen?: string | null;
  fechaPuesta?: dayjs.Dayjs;
}

export class Banner implements IBanner {
  constructor(
    public id?: number,
    public imagenContentType?: string | null,
    public imagen?: string | null,
    public fechaPuesta?: dayjs.Dayjs
  ) {}
}

export function getBannerIdentifier(banner: IBanner): number | undefined {
  return banner.id;
}
