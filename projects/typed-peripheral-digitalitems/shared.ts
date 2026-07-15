export declare type IDInfo = {
  currentTime: number;
  digitizedAt: number;
  decaysAt: number;
  lastRefresh: number;
};

export declare type ItemIDInfo = IDInfo & {
  item: ItemDetail;
};
