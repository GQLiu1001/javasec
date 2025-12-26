# NIO

## 三大组件

### Channel

读写数据双向通道

常见的 Channel

- FileChannel 

> 文件

- DatagramChannel 

> UDP网络编程

- SocketChannel 

> TCP传输通道

- ServerSocketChannel 

> 服务器专用TCP传输通道

### Buffer

暂存 Channel 传输的数据 缓冲读写数据

最常见 

- ByteBuffer
  - MappedByteBuffer
  - DirectByteBuffer
  - HeapByteBuffer

### Selector

选择器 配合一个线程来管理多个 Channel

# ByteBuffer

ByteBuffer 由三个部分组成

- 永远不变的 `capacity`（容量）
- `position`：下一个要**读 / 写**的字节位置（类似数组下标）
- `limit`：最多能**读 / 写**到哪个位置（超过就报错）

> 初始化是写模式，将数据写入缓冲区，此时 position 移动，limit (写入限制)在capacity处。写完之后，也就是切换到读模式时，filp 之后 position 再次从头开始移动，但是 limit (读取限制)位置变为缓冲数据所在处。读取全部字节 limit 和 position 重合后 
>
> clear 切换到写模式 position 回到头 limit (写入限制)移到尾。开始写模式
>
> compact 把未读完的数据向前压缩 也就是将未读完的数据从头排列 然后 position 从未读完的数据最后一个向右移动。开始写模式

> 注意不会清除数据 只是重置指针位置 发生覆盖
>
> get/put 一下 position 就移动一下

核心模式切换

| 方法             | 核心作用                  | 指针变化（核心）                                            |
| ---------------- | ------------------------- | ----------------------------------------------------------- |
| `flip()`         | 写模式 → 读模式           | position=0；limit = 原 position；mark=-1                    |
| `clear()`        | 读模式 → 写模式（重置）   | position=0；limit=capacity；mark=-1                         |
| `compact()`      | 读模式 → 写模式（保数据） | 未读数据移到缓冲区开头；position = 未读长度；limit=capacity |
| `rewind()`       | 读模式内重置读取位置      | position=0；limit 不变；mark=-1                             |
| `hasRemaining()` | 判断是否有可读数据        | 无指针变化，返回 `position < limit`                         |

## 初始化

```java
ByteBuffer buffer = ByteBuffer.allocate(1024);
```

- position = 0（下一个写入位置在 0）
- limit = 1024（最多写到 1024）
- capacity = 1024（固定不变）

## 写数据

```java
int readBytes = channel.read(buffer); // 假设文件一次写了500字节到缓冲区
```

- position 从 0 → 500（写了 500 字节，下一个写入位置到 500）
- limit 还是 1024
- capacity = 1024（固定不变）

## 读模式

```java
buffer.flip();
```

- position 从 500 → 0（读取起点回到缓冲区开头）
- limit 从 1024 → 500（把 “写的终点” 改成 “读的终点”，避免读空字节）
- capacity = 1024（固定不变）

为什么必须用 flip ()：不 flip 的话，读数据会从 position=500 开始，而 limit=1024，你会读到 500-1024 的空字节，完全错了。flip () 就是 “把缓冲区的读写方向反过来”。

## 读数据

```java
// 批量读取所有数据
// remaining() = limit - position = 500
byte[] data = new byte[buffer.remaining()]; 
buffer.get(data);
```

- position 从 0 → 500（读完了所有 500 字节）
- limit 还是 500
- capacity = 1024（固定不变）

## 切回默认写模式

### 场景 A：数据已全部读完 → 用 clear ()

```java
buffer.clear();
```

- position 从 500 → 0（写入起点归 0）
- limit 从 500 → 1024（写的终点拉满到容量）
- capacity = 1024（固定不变）

### 场景 B：数据没读完（比如只读了 200 字节）→ 用 compact ()

```java
// 假设只读了200字节，此时position=200，limit=500（还有300字节没读）
buffer.compact();
```

先把未读的 300 字节（position=200 到 limit=500）移到缓冲区开头（0-299）；

- position 设为 300（下一次写数据从 300 开始，不覆盖未读数据）；

- limit 重置为 1024（写的终点拉满）
- capacity = 1024（固定不变）

## HeapByteBuffer/DirectByteBuffer 

class java.nio.HeapByteBuffer

- java堆内存，读写效率较低，受到GC的影响

classjava.nio.DirectByteBuffer 

- 直接内存，读写效率高(少一次拷贝)，不会受GC影响,
  分配的效率低

## 主要方法

| 方法      | 介绍                             |
| --------- | -------------------------------- |
| `mark()`  | 标记                             |
| `reset()` | 重置 position 到上次做标记的地方 |
| `get(i)`  | 读 position 不移动               |
