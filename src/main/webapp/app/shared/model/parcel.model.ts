import { IContainer } from 'app/shared/model/container.model';
import { ParcelSize } from 'app/shared/model/enumerations/parcel-size.model';

export interface IParcel {
  id?: string;
  parcelNumber?: string | null;
  size?: ParcelSize | null;
  container?: IContainer | null;
}

export const defaultValue: Readonly<IParcel> = {};
