### 命令

#### run

编译、链接、运行go程序

例如：go run main.go

#### build

编译、链接成exe文件

例如：go build main.go



### 关键字

#### len(v Type) int

元素的长度

```go
// Array: the number of elements in v.
// Pointer to array: the number of elements in *v (even if v is nil).
// Slice, or map: the number of elements in v; if v is nil, len(v) is zero.
// String: the number of bytes in v.
// Channel: the number of elements queued (unread) in the channel buffer;
//          if v is nil, len(v) is zero.
```

#### make(t Type, size ...IntegerType) Type

分配并初始化一个对象，返回参数类型的对象

例如：counts := make(map[string]int)

```go
//  Slice: The size specifies the length. The capacity of the slice is
// equal to its length. A second integer argument may be provided to
// specify a different capacity; it must be no smaller than the
// length. For example, make([]int, 0, 10) allocates an underlying array
// of size 10 and returns a slice of length 0 and capacity 10 that is
// backed by this underlying array.
// Map: An empty map is allocated with enough space to hold the
// specified number of elements. The size may be omitted, in which case
// a small starting size is allocated.
// Channel: The channel's buffer is initialized with the specified
// buffer capacity. If zero, or the size is omitted, the channel is
// unbuffered.
```



### os

#### slice os.Args

命令行参数，第一个是程序路径，后面的是参数

例如：

```go
	for i := 1; i < len(os.Args); i++ {
		s += sep + os.Args[i]
		sep = " "
	}
```



### strings

#### strings.Join(elems []string, sep string) string

拼接字符串，sep是分隔符

例如：strings.Join(os.Args[1:], " ")



### fmt

#### fmt.Println(string)

打印到标准输出流

例如：fmt.Println("Hello, World!")



